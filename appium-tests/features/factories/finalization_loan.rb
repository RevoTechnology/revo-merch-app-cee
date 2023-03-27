FactoryBot.define do
  factory :finalization_loan do
    agree_processing { '1' }
    confirmation_code { '1111' }
    agree_sms_info { '0' }
    employee_id { 'GAY13' }
  end
end
