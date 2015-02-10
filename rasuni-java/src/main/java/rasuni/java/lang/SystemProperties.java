package rasuni.java.lang;

/**
 * System properties utilities
 */
public final class SystemProperties
{
	private SystemProperties()
	{
		// disallow construction
	}

	/**
	 * Gets the system property indicated by the specified key. Return null if system property does not exist.
	 * @param key the key
	 * @return he system property indicated by the specified key
	 */
	public static String get(String key)
	{
		return System.getProperty(key, null);
	}
}
