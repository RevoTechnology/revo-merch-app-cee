module Types
  include Dry.Types
end

class Loan < Dry::Struct
  attribute :store_id, Types::Strict::String
  attribute :order_id, Types::Strict::String
  attribute :employee_id, Types::Strict::String
  attribute :amount, Types::Strict::String
  attribute :mobile_phone, Types::Strict::String
  attribute :agree_insurance, Types::Strict::String
end

class CartItem < Dry::Struct
  attribute :name, Types::Strict::String
  attribute :price, Types::Strict::String
  attribute :quantity, Types::Strict::String
end

class AdditionalData < Dry::Struct
  attribute :channel, Types::Strict::String
  attribute :issued_loans, Types::Strict::String
  attribute :previous_url, Types::Strict::String
end
