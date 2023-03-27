FactoryBot.define do
  sequence(:passport_number, 111_111, &:to_s)
  factory :person do
    first_name { Faker::Name.male_first_name }
    middle_name { Faker::Name.male_middle_name }
    last_name { Faker::Name.male_last_name }
    email { Faker::Internet.email }
    mobile_phone { ENV.fetch('PHONE') }
    birth_date { '01-02-1984' }
    area { Faker::Address.city }
    settlement { Faker::Address.city }
    street { Faker::Address.street_title }
    house { Faker::Address.building_number }
    building { Faker::Address.building_number }
    apartment { Faker::Address.building_number }
    id_documents do
      {
        russian_passport: {
          series: '4214',
          number: generate(:passport_number)
        }
      }
    end
  end
end
