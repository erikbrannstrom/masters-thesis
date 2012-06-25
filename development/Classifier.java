import java.util.*;

public interface Classifier
{
	
	public void learn(List<Instance> instances, Attribute classAttribute);
	public String classify(Instance instance);

}