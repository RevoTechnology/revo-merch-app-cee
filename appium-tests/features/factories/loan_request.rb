FactoryBot.define do
  factory :loan do
    store_id {}
    order_id { 'R2424' }
    employee_id { ENV.fetch('EMPLOYEE_ID') }
    mobile_phone { ENV.fetch('PHONE') }
    amount { '3000' }

    trait :with_insurance do
      agree_insurance { '1' }
    end
  end
end

FactoryBot.define do
  factory :cart_item do
    name { 'Товар автотест' }
    price { '23000' }
    quantity { '1' }
  end
end

FactoryBot.define do
  factory :additional_data do
    channel { 'mobile' }
    issued_loans { '1' }
    previous_url { 'https://revo.ru/where-to-buy' }
  end
end
