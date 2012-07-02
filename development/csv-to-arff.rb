require 'csv'

rows = Array.new
first = true

CSV.foreach("data/mexico-full.csv") do |row|
  if first then
    first = false
    next
  end

  weight = 1*row[4].to_i

  puts row[0] + ',' + row[1] + ',' + row[2] + ',no, {' + (row[3].to_i-weight).to_s + '}'
  puts row[0] + ',' + row[1] + ',' + row[2] + ',yes, {' + weight.to_s + '}'
end