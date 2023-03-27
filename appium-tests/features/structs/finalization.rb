module Types
  include Dry.Types
end

class FinalizationLoan < Dry::Struct
  attribute :agree_processing, Types::Strict::String
  attribute :confirmation_code, Types::Strict::String
  attribute :agree_sms_info, Types::Strict::String
  attribute :employee_id, Types::Strict::String
end
