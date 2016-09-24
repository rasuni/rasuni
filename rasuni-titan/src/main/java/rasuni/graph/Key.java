package rasuni.graph;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

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
		return getProperty(entry, _name);
	}

	@SuppressWarnings("unchecked")
	private T getProperty(Element element, String name)
	{
		return ((Property<T>) element.property(name)).value();
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
