file = ARGV[0]

dict = Hash.new
f = File.open(file, "r")
f.each_line { |line|
  line.split.each { |w|
    w.gsub!(/\W+/, '') # trim non-word characters
    next if w.length == 0 # skip empty strings
    w.downcase! # all lowercase
    if dict.has_key?(w)
      dict[w] = dict[w] + 1
    else
      dict[w] = 1
    end
  }
}

# sort the hash by value, and then print it in this sorted order
dict.sort{|a,b| a[1]<=>b[1]}.each { |elem|
  puts "\"#{elem[0]}\" has #{elem[1]} occurrences"
}
puts "#{dict.length} words in total."