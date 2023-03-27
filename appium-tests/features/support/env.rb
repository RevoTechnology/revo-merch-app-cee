require 'appium_lib'
require 'dotenv'
require 'faker'
require 'faraday'
require 'factory_bot'
require 'dry-struct'

environment = ENV.fetch('ENV', 'staging_best')
Dotenv.load(".env.#{environment}")

include FactoryBot::Syntax::Methods
Faker::Config.locale = :ru

def caps
  {
    caps: {
      automationName: 'UiAutomator2',
      platformName: 'Android',
      deviceName: 'Lenovo',
      app: File.join(File.dirname(__FILE__), 'revo_release_stage_2.0.5.apk'),
      appPackage: 'pl.revo.merchant',
      appActivity: 'pl.revo.merchant.ui.root.RootActivity',
      autoGrantPermissions: true
    }
  }
end

Appium::Driver.new(caps, true)
Appium.promote_appium_methods Object
