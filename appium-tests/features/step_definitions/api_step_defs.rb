api_client = Faraday.new(ENV.fetch('DOMAIN'))

Если(/создает займ с параметрами$/) do |table|
  loan_request_table = table.hashes[0]
  insurance_option = loan_request_table.fetch('insurance_option')
  store_id = loan_request_table.fetch('store_id')
  phone = loan_request_table.fetch('phone')
  request = {
    user: attributes_for(:user_login, login: "7#{ENV.fetch('AGENT_LOGIN')}", password: ENV.fetch('AGENT_PASSWORD'))
  }
  response = api_client.post('api/loans/v1/sessions', request)
  @token = JSON.parse(response.body)['user']['authentication_token']
  create_loan_request(api_client, insurance_option, store_id, phone)
  create_client(api_client)
  create_approved_loan(api_client)
  upload_docs(api_client)
  send_confirmation_code(api_client)
  finalize_loan(api_client)
end
