import java.util.*;
import weka.core.*;

public interface Scenario
{
	public String header(int col);
	public List<Double> data(int col);
	public int columns();
}