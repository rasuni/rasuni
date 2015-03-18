package rasuni.java.lang;

import java.util.Properties;

/**
 * The system utilities
 *
 */
public final class System
{
	private System()
	{
		// do not allow creation
	}

	/**
	 * Check the key
	 * @param key the key to check
	 */
	public static void checkKey(java.lang.String key)
	{
		if (key == null)
		{
			throw new NullPointerException("key can't be null");
		}
		boolean equals = key.equals("");
		if (equals)
		{
			throw new IllegalArgumentException("key can't be empty");
		}
	}

	/**
	 * Gets the system property indicated by the specified key.
	 * <p>
	 * First, if there is a security manager, its
	 * <code>checkPropertyAccess</code> method is called with the
	 * <code>key</code> as its argument.
	 * <p>
	 * If there is no current set of system properties, a set of system
	 * properties is first created and initialized in the same manner as
	 * for the <code>getProperties</code> method.
	 *
	 * @param      key   the name of the system property.
	 * @param security the security manager
	 * @param props  the properties
	 * @param      def   a default value.
	 * @return     the string value of the system property,
	 *             or the default value if there is no property with that key.
	 *
	 * @exception  SecurityException  if a security manager exists and its
	 *             <code>checkPropertyAccess</code> method doesn't allow
	 *             access to the specified system property.
	 * @exception  NullPointerException if <code>key</code> is
	 *             <code>null</code>.
	 * @exception  IllegalArgumentException if <code>key</code> is empty.
	 * @see        java.lang.System#setProperty
	 * @see        java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
	 * @see        java.lang.System#getProperties()
	 */
	public static String getProperty(java.lang.String key, SecurityManager security, Properties props, String def)
	{
		checkKey(key);
		if (security != null)
		{
			security.checkPropertyAccess(key);
		}
		return props.getProperty(key, def);
	}

	/**
	 * Gets the system property indicated by the specified key. Return null if system property does not exist.
	 * @param key the key
	 * @param security the security manager
	 * @param props the properties
	 * @return he system property indicated by the specified key
	 */
	public static String getProperty(String key, SecurityManager security, Properties props)
	{
		return getProperty(key, security, props, null);
	}
}
