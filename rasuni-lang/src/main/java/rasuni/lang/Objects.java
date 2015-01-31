package rasuni.lang;


/**
 * Utilities available for all objects
 */
public final class Objects
{

	private Objects () {
		// disallow construction
	}
	
	static 
	{
		@SuppressWarnings("unused") // 100% test coverage
		Objects objects = new Objects ();
	}
	
	/**
	 * Return true if the the object is null
	 * @param object the object
	 * @return true if the object is null
	 */
	public static boolean isNull(Object object)
	{
		return object == null;
	}

	/**
	 * Check sequence for existing members
	 * @param object  the object
	 * @return true if members do exist
	 */
	public static boolean notNull(Object object)
	{
		return object != null;
	}

}
