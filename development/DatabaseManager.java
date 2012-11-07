import weka.core.converters.DatabaseLoader;
import weka.core.converters.DatabaseSaver;

public class DatabaseManager implements DataManager
{
	private String tableName;

	public DatabaseManager(String tableName)
	{
		this.tableName = tableName;
	}

	public void add(Ads ads)
	{
		try {
			DatabaseSaver save = new DatabaseSaver();
			save.setInstances(ads);
			save.setRelationForTableName(false);
			save.setTableName(this.tableName);
			save.connectToDatabase();
			save.writeBatch();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Ads get()
	{
		try {
			DatabaseLoader loader = new DatabaseLoader();
			loader.connectToDatabase();
			loader.setQuery(String.format("SELECT Body, Image_Hash, Clicks_Count, Impressions FROM %s WHERE Gender = 'Women'", this.tableName));
			return new Ads(loader.getDataSet());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}