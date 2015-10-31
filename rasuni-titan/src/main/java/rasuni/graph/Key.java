package rasuni.graph;

import com.tinkerpop.blueprints.Element;
import rasuni.titan.TitanCollector;

/**
 * Property key
 *
 * @author Ralph Sigrist
 *
 * @param <T>
 *            key tpye
 */
public final class Key<T>
{
	private final String _name;

	/**
	 * the key type
	 */
	public final Class<T> _type;

	/**
	 * the is complete field
	 */
	public final static Key<Boolean> IS_COMPLETE = new Key<>("isComplete", Boolean.class);

	/**
	 * the acoust id key
	 */
	public final static Key<String> ACOUST_ID = TitanCollector.string("acoust.id");

	/**
	 * Constructor
	 *
	 * @param name
	 *            the key name
	 * @param type
	 *            the key type
	 */
	public Key(String name, Class<T> type)
	{
		_name = name;
		_type = type;
	}

	/**
	 * get property value
	 *
	 * @param entry
	 *            the entry
	 * @return the property value
	 */
	public T get(Element entry)
	{
		return entry.getProperty(_name);
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
