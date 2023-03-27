Допустим(/^консультант логинится в приложении с данными '(.*)' и '(.*)'$/) do |login, pin|
  find_element(id: 'signInLogin').send_keys(ENV.fetch(login))
  find_element(id: 'signInPin').send_keys(ENV.fetch(pin))
end

Если(/нажимает кнопку ВОЙТИ$/) do
  find_element(id: 'signInBtn').click
end

То(/открывается экран '(.*)'$/) do |screen|
  expect(find_element(id: 'rootTitle').text).to eq(screen)
end

Если(/нажимает на элемент ОФОРМЛЕНИЕ КЛИЕНТА \/ ЗАКАЗА$/) do
  find_element(id: 'dashboardMakeCard').click
end

И(/нажимает на элемент ВОЗВРАТ ЗАКАЗА$/) do
  find_element(id: 'dashboardReturnCard').click
end

И(/вводит номер клиента '(.*)'$/) do |phone|
  ENV.store('PHONE', ENV.fetch(phone))
  find_element(id: 'purchasePhone').send_keys(ENV.fetch(phone)[1..])
end

И(/нажимает кнопку ПРОДОЛЖИТЬ$/) do
  find_element(id: 'button_progress').click
end

Если(/заполняет данные о клиенте$/) do
  client = attributes_for(:person)
  find_element(id: 'profileLastName').send_keys(client[:last_name])
  find_element(id: 'profileFirstName').send_keys(client[:first_name])
  find_element(id: 'profileMiddleName').send_keys(client[:middle_name])
  find_element(id: 'profileId').send_keys(
    "#{client[:id_documents][:russian_passport][:series]}#{client[:id_documents][:russian_passport][:number]}"
  )
  find_element(id: 'profileBirth').send_keys(client[:birth_date].gsub('-', ''))
end

Если(/соглашается с условиями предоставления услуг$/) do
  find_element(id: 'profileAgreeAsp').click
end

И(/вводит код подтверджения$/) do
  key_one = find_element(id: 'keyboardOne')
  4.times { key_one.click }
end

Если(/вводит сумму покупки '(.*)' рублей$/) do |amount|
  @amount = amount
  find_element(id: 'calcSum').send_keys(@amount)
end

И(/нажимает кнопку ОБНОВЛЕНИЕ ТАРИФОВ$/) do
  find_element(id: 'calcRefresh').click
end

А(/выбирает график платежей$/) do
  find_element(id: 'loanHeaderLayout').click
end

Если(/консультант загружает фото документов$/) do
  doc_field_ids = %w[documentsNameImage documentsLivingAddressImage documentsClientWithPassportImage]
  doc_field_ids.each do |field_id|
    find_element(id: field_id).click
    find_element(id: 'pictureButton').click
    find_element(id: 'savePhotoButton').click
  end
end

И(/выбирает торговую точку '(.*)'$/) do |store|
  find_element(xpath: "//*[@class='android.widget.TextView' and @text[contains(.,'#{store}')]]").click
end

Если(/соглашается с условиями Договора$/) do
  find_element(id: 'contractRuAgreeCheck').click
end

То(/на экране показывается сумма покупки$/) do
  result_amount = find_element(id: 'barcodeTotalSum').text.gsub(/[^0-9]/, '').to_i
  expect(result_amount).to eq(@amount.to_i)
end

Допустим(/на экране показыватся штрих\-код$/) do
  expect(find_element(id: 'barcodeImg')).to be_truthy
  expect(find_element(id: 'barcodeValue')).to be_truthy
end

И(/нажимает кнопку ЗАВЕРШИТЬ$/) do
  find_element(id: 'barcodeNextBtn').click
end

Если(/^консультант вводит '(.*)' для поиска займа$/) do |phone|
  find_element(id: 'searchLogin').send_keys(ENV.fetch(phone))
end

И(/^нажимает кнопку ИСКАТЬ$/) do
  find_element(id: 'searchButton').click
end

А(/нажимает кнопку ОФОРМИТЬ ВОЗВРАТ$/) do
  find_element(id: 'searchItemMakeReturn').click
end

Если(/выбирает полный возврат$/) do
  find_element(id: 'detailFullReturn').click
end

То(/^поле К ВОЗВРАТУ заполняется полной суммой заказа$/) do
  result_amount = find_element(id: 'detailReturnText').text.gsub(/[^0-9]/, '')
  expect(result_amount.to_i).to eq(@loan_request[:amount].to_i)
end
