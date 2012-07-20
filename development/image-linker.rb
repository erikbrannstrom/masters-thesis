require 'csv'

file = File.new("data/facebook-ads.csv")
parser = CSV.new(file, { :headers => true, :return_headers => true, :header_converters => :symbol })

csv_out = CSV.open("data/facebook-ads-modified.csv", "wb")

countries = { "PL" => "Poland", "AR" => "Argentina", "MX" => "Mexico", "BR" => "Brazil" }

csv_out << parser.readline()

parser.each do |row|
	country = countries[row[:country]]
	image = row[:image]

	if image.end_with?(".jpg") || image.end_with?(".jpeg") || image.end_with?(".png")
		pictures = Dir.glob("../Misc/Marketing-images/" + country + "/Delivered/**/" + image)
	else
		pictures = Dir.glob("../Misc/Marketing-images/" + country + "/Delivered/**/" + image + ".{jpg,jpeg,png}")
	end

	if pictures.length > 0
		row[:image] = pictures[0].sub("../Misc/Marketing-images/", "")
	else
		row[:image] = ""
	end

	csv_out << row
end

csv_out.close()
file.close()