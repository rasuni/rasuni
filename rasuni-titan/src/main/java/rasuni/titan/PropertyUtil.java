package rasuni.titan;

import java.util.Properties;

/**
 * Utilities for properties
 *
 */
public final class PropertyUtil
{
	private PropertyUtil()
	{
		// disallow construction
	}

	/**
	 * Read a property with the specified key. Returns null when property does not exist.
	 * @param properties the properties
	 * @param key the key
	 * @return property value or null if not found
	 */
	public static String get(Properties properties, String key)
	{
		return properties.getProperty(key, null);
	}
}
