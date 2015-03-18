package rasuni.java.lang;

/**
 * A function with a single parameter
 */
public interface IFunction
{
	/**
	 * Apply the function to the parameter
	 * @param parameter the parameter
	 * @return the result
	 */
	char[] apply(String parameter);
}
