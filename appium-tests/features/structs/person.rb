module Types
  include Dry.Types
end

class Person < Dry::Struct
  attribute :id, Types::Strict::String
  attribute :mobile_phone, Types::Strict::String
  attribute :phone, Types::Strict::String
  attribute :first_name, Types::Strict::String
  attribute :middle_name, Types::Strict::String
  attribute :last_name, Types::Strict::String
  attribute :birth_date, Types::Strict::String
  attribute :email, Types::Strict::String
  attribute :area, Types::Strict::String
  attribute :settlement, Types::Strict::String
  attribute :street, Types::Strict::String
  attribute :house, Types::Strict::String
  attribute :building, Types::Strict::String
  attribute :apartment, Types::Strict::String
  attribute :id_documents do
    attribute :russian_passport do
      attribute :series, Types::Strict::String
      attribute :number, Types::Strict::String
    end
  end
  attribute :customer_ability, Types::Strict::Float
  attribute :GUID, Types::Strict::String
  attribute :pre_filled, Types::Strict::String
end
