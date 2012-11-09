
public interface DataManager
{
	public void add(Ads ads);
	public Ads get();
	public void where(String key, String value);
	public void resetWhere();
}