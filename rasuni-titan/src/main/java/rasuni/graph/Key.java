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
	public final String _name;

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

	@SuppressWarnings("unchecked")
	public T getProperty(Element element, String name)
	{
		return ((Property<T>) element.property(name)).value();
	}
}
