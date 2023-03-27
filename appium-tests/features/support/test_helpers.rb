options = Selenium::WebDriver::Chrome::Options.new
options.add_argument('--headless') # comment to disable headless mode
browser = Selenium::WebDriver.for(:chrome, options: options)

browser.manage.timeouts.implicit_wait = 5
browser.manage.window.resize_to(1600, 1000)

Before('@check_exist_client_before_test') do
  login_to_admin_panel(browser)
  phones = [
    ENV.fetch('MOBILE_PHONE_ACCEPT'),
    ENV.fetch('MOBILE_PHONE_DECLINE')
  ]
  phones.each { |phone| delete_client_if_exits(browser, phone[1..]) }
end

After('@delete_client_after_test') do
  login_to_admin_panel(browser)
  delete_client_if_exits(browser, ENV.fetch('PHONE')[1..])
end

def login_to_admin_panel(browser)
  browser.navigate.to("#{ENV.fetch('DOMAIN')}admin")
  current_url = browser.current_url
  fill_in_login_form(browser) if current_url == "#{ENV.fetch('DOMAIN')}users/sign_in"
end

def fill_in_login_form(browser)
  browser.find_element(id: 'user_login').send_keys(ENV.fetch('USER_NAME'))
  browser.find_element(id: 'user_password').send_keys(ENV.fetch('USER_PWD'))
  browser.find_element(name: 'commit').click
end

def delete_client_if_exits(browser, phone)
  find_client_by_phone(browser, phone)
  delete_link = browser.find_elements(class: 'delete_link')
  accept_delete_client(browser, delete_link) unless delete_link.empty?
end

def find_client_by_phone(browser, phone)
  browser.navigate.to("#{ENV.fetch('DOMAIN')}admin/clients")
  browser.find_element(id: 'q_with_mobile_phone_from_string').send_keys(phone)
  browser.find_element(name: 'commit').click
end

def accept_delete_client(browser, delete_link)
  delete_link[0].click
  browser.switch_to.alert.accept
end

def define_loan_request_trait(insurance_option)
  if insurance_option == 'со страховкой'
    attributes_for(:loan, :with_insurance, store_id: ENV.fetch(@store_id))
  elsif insurance_option == 'без страховки'
    attributes_for(:loan, store_id: ENV.fetch(@store_id))
  else
    raise StandardError, 'Unknown insurance_option'
  end
end

def create_loan_request(api_client, insurance_option, store_id, phone)
  ENV.store('PHONE', ENV.fetch(phone))
  @store_id = store_id
  @loan_request = define_loan_request_trait(insurance_option)
  request = {
    loan_request: @loan_request,
    cart_items: [
      attributes_for(:cart_item)
    ],
    additional_data: attributes_for(:additional_data)
  }
  @response = api_client.post('api/loans/v1/loan_requests', request, 'Authorization': @token)
  @loan_token = JSON.parse(@response.body)['loan_request']['token']
end

def create_client(api_client)
  @client = attributes_for(:person)
  request = {
    client: @client
  }
  @response = api_client.post("api/loans/v1/loan_requests/#{@loan_token}/client", request, 'Authorization': @token)
end

def create_approved_loan(api_client)
  request = { term_id: ENV.fetch('TERM_ID') }
  @response = api_client.post("api/loans/v1/loan_requests/#{@loan_token}/loan", request, 'Authorization': @token)
end

def upload_docs(api_client)
  params = {
    client: {
      documents: {
        "name": File.open('1.jpg'),
        "living_addr": File.open('2.jpg'),
        "client_with_passport": File.open('3.jpg'),
        "previous_passport": File.open('4.jpg'),
        "issued_by": File.open('5.jpg'),
        "first_two_pages": File.open('6.jpg')
      }
    }
  }
  @response = api_client.patch("api/loans/v1/loan_requests/#{@loan_token}/client", params, 'Authorization': @token)
end

def send_confirmation_code(api_client)
  @response = api_client.post("api/loans/v1/loan_requests/#{@loan_token}/client/confirmation", {}, 'Authorization':
    @token)
end

def finalize_loan(api_client)
  request = {
    loan: attributes_for(:finalization_loan)
  }
  @response = api_client.post("api/loans/v1/loan_requests/#{@loan_token}/loan/finalization", request, 'Authorization':
    @token)
end
