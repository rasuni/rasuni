package rasuni.graph;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Element;
import rasuni.titan.TitanCollector;

/**
 * A key for enumeration values
 *
 * @param <T>
 *            the enumeration type
 *
 */
public final class EnumKey<T>
{
	/**
	 * The name
	 */
	private final String _name;

	private final T[] _values;

	/**
	 * Constructor
	 *
	 * @param name
	 *            the key name
	 * @param values
	 *            the values
	 */
	public EnumKey(String name, T[] values)
	{
		_name = name;
		_values = values;
	}

	/**
	 * Return the ordinal value
	 *
	 * @param element
	 *            the element to read
	 * @return the ordinal value
	 */
	public T get(Element element)
	{
		return _values[TitanCollector.key(this).get(element)];
	}

	/**
	 * define the key in the databasde
	 *
	 * @param tg
	 *            the titnal graph the database
	 */
	public void makePropertyKey(TitanGraph tg)
	{
		TitanCollector.key(this).makePropertyKey(tg);
	}

	/**
	 * Return the name
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return _name;
	}
}
