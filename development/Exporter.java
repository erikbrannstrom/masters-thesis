import java.io.*;
import java.util.*;

public class Exporter
{
	private File templateFile;
	private String headerLine, rowTemplate;

	public Exporter(File templateFile)
	{
		this.templateFile = templateFile;
	}

	public Exporter(String templateFile)
	{
		this(new File(templateFile));
	}

	public void export(File outputFile, List<Map<String,String>> values)
	{
		// Read template
		if (this.headerLine == null) {
			try {
				Scanner scanner = new Scanner(this.templateFile);
				this.headerLine = scanner.nextLine();
				this.rowTemplate = scanner.nextLine();
				scanner.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		// Loop through values and create new file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(this.headerLine);
			writer.newLine();
			for (Map<String,String> map : values) {
				String line = this.rowTemplate;
				for (String key : map.keySet()) {
					line = line.replaceAll(String.format("\\{%s\\}", key), map.get(key));
				}
				writer.write(line);
				writer.newLine();
				writer.flush();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}