# coding: UTF-8
require 'CSV'
tag_file = ARGV[1]

f = File.open(tag_file, 'r')
tags = Hash.new
f.each_line { |line|
  split = line.split(':')
  tag = split[0].strip
  keywords = split[1].split(',')
  keywords.map! { |keyword| keyword.strip }
  tags[tag] = keywords
}
first = true
CSV.foreach(ARGV[0], { :headers => true }) do |row|
  tags.each { |tag, keywords|
    row["body-#{tag}"] = 0
    row["title-#{tag}"] = 0
  }
  title_tags = Array.new
  body_tags = Array.new
  row['Body'].split.each { |w|
    w.gsub!(/[^\p{L}]/, '') # trim non-word characters
    next if w.length == 0 # skip empty strings
    w.downcase! # all lowercase
    tags.each { |tag, keywords|
      if keywords.include?(w)
        row["body-#{tag}"] = 1
        #body_tags << tag
      end
    }
  }
  row['Title'].split.each { |w|
    w.gsub!(/[^\p{L}]/, '') # trim non-word characters
    next if w.length == 0 # skip empty strings
    w.downcase! # all lowercase
    tags.each { |tag, keywords|
      if keywords.include?(w)
        row["title-#{tag}"] = 1
        #title_tags << tag
      end
    }
  }

  row.delete('Title')
  row.delete('Body')
  # for i in 0..9
  #   if i < body_tags.length
  #     row["body-tag-#{i+1}"] = body_tags[i]
  #   else
  #     row["body-tag-#{i+1}"] = '?'
  #   end
  # end

  # for i in 0..2
  #   if i < title_tags.length
  #     row["title-tag-#{i+1}"] = body_tags[i]
  #   else
  #     row["title-tag-#{i+1}"] = '?'
  #   end
  # end
  puts row.headers if first
  first = false
  puts row
end