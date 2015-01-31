package rasuni.check;

import rasuni.titan.TitanCollector;

/**
 * @author Ralph Sigrist
 *
 */
public final class Assert
{
	/**
	 * Expect a true condition
	 *
	 * @param expected
	 *            expected to be true
	 */
	public static void expect(boolean expected)
	{
		TitanCollector.failIf(!expected);
	}
}
