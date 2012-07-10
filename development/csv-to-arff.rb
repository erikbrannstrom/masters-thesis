require 'csv'

rows = Array.new
first = true

CSV.foreach("data/mexico-cities.csv") do |row|
  if first then
    first = false
    next
  end

  weight = 1*row[5].to_i

  puts row[0] + ',' + row[1] + ',' + row[2] + ',' + row[3] + ',no, {' + (row[4].to_i-weight).to_s + '}'
  puts row[0] + ',' + row[1] + ',' + row[2] + ',' + row[3] + ',yes, {' + weight.to_s + '}'
end