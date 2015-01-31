package rasuni.webservice;

/**
 * A query parameter
 *
 */
public final class Parameter
{
	final String _name;

	final String _value;

	/**
	 * Constructor
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public Parameter(String name, String value)
	{
		_name = name;
		_value = value;
	}

	/**
	 * Construct an include parameter
	 * @param includes the includes
	 * @return the constructed parameter
	 */
	public static Parameter inc(final String includes)
	{
		return new Parameter("inc", includes);
	}
}
