module Types
  include Dry.Types
end

class UserLogin < Dry::Struct
  attribute :login, Types::Strict::String
  attribute :password, Types::Strict::String
end
