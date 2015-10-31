package rasuni.webservice;

/**
 * A query parameter
 *
 */
public final class Parameter
{
	/**
	 * the parameter name
	 */
	public final String _name;

	/**
	 * the parameter value
	 */
	public final String _value;

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
}
