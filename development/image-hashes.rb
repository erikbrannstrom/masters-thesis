require 'find'
require 'digest/md5'

pdf_file_paths = []
Find.find('../../team duego') do |path|
  if path =~ /.*\.(jpg|jpeg|png)$/
  	puts path
  	puts Digest::MD5.hexdigest(File.read(path))
  end
end