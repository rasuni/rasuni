package rasuni.graph;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.schema.SchemaManager;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.VertexQuery;
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

	private final Class<T> _type;

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
	 * Has vertex query
	 *
	 * @param query
	 *            the query
	 * @param value
	 *            the search value
	 * @return the new query
	 */
	public VertexQuery has(VertexQuery query, T value)
	{
		return query.has(_name, value);
	}

	/**
	 * Register a property key
	 *
	 * @param tg
	 *            the titan graph
	 * @return the titan key
	 */
	public PropertyKey makePropertyKey(SchemaManager tg)
	{
		return TitanCollector.definePropertyKey(this, tg, () -> null, tk -> tk);
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

	/**
	 * Return the type
	 *
	 * @return the type
	 */
	public Class<?> getType()
	{
		return _type;
	}
}
