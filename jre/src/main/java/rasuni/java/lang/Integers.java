package rasuni.java.lang;

/**
 * Integer functions
 */
public final class Integers
{
	private Integers()
	{
		// disallow construction
	}

	/**
	 * The integer identity function
	 * @param p the parameter
	 * @return the parameter integer
	 */
	public static int identity(int p)
	{
		return p;
	}

	/**
	 * Test if the integer value is negative
	 * @param value the value to test
	 * @return true if the value is negative
	 */
	public static boolean isNegative(int value)
	{
		return value < 0;
	}
}
