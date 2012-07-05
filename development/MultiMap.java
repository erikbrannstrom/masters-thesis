
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

public class MultiMap<K, V>
{
	private Map<K, List<V>> map;

	public List<V> get(K key)
	{
		return this.map.get(key);
	}

	public void put(K key, V value)
	{
		if (this.map.containsKey(key)) {
			this.get(key).add(value);
		} else {
			List<V> list = new LinkedList<V>();
			list.add(value);
			this.map.put(key, list);
		}
	}

	public Set<K> keySet()
	{
		return this.map.keySet();
	}

	public int size(K key)
	{
		return (this.get(k) == null) ? 0 : this.get(k).size();
	}

}