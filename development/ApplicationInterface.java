
public class ApplicationInterface
{
	private Estimator estimator;
	private AdFactory ads;
	private Instances estimatedAds;

	public ApplicationInterface(Estimator estimator, AdFactory ads)
	{
		this.estimator = estimator;
		this.ads = ads;
	}

	public Instances estimatedAds()
	{
		if (this.estimatedAds == null) {
			this.estimatedAds = this.ads.all();
			for (Instance ad : this.estimatedAds) {
				ad.setClassValue(estimator.estimate(ad));
			}
		}
		return this.estimatedAds;
	}

}