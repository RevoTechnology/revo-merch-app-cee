Before do
  $driver.start_driver
  $driver.manage.timeouts.implicit_wait = 5
end

After do
  $driver.driver_quit
end
