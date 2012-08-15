# coding: UTF-8
filtered = []

while (line = gets) != nil do
  match = line.match('"(.+)" has (\d+)')
  puts "#{match[1]}: #{match[1]}" unless filtered.include?(match[1])
end