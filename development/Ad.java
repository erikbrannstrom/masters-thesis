
public class Ad extends Instance
{

	public Ads dataset()
	{
		return (Ads)super.dataset();
	}

	public double actionRate()
	{
		Ads data = this.dataset();
		if (data.attribute("ActionRate") != null) {
			return this.value(data.attribute("ActionRate"));
		} else if (data.attribute("Actions") != null && data.attribute("Impressions") != null) {
			return this.value(data.attribute("Actions")) / this.value(data.attribute("Impressions"));
		} else {
			throw new RuntimeException("No comparison action rate could be found.");
		}
	}

}