package rasuni.taskqueue;

/**
 * @author Ralph Sigrist
 *
 */
public final class Assert
{

	/**
	 * Expect a true condition
	 * @param expected expected to be true
	 */
	public static void expect(boolean expected)
	{
		if (!expected)
		{
			fail();
		}
	}

	/**
	 * Assertion failed
	 */
	public  static void fail()
	{
		throw new RuntimeException("Unexpected");
	}
}
