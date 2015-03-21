package rasuni.java.lang;

/**
 * Helpers for string out of bound exception
 *
 */
public final class StringIndexOutOfBoundsExceptions
{
	/**
	 * Throws a string out of bounds exception when the provided lower value is smaller than the higher value
	 * @param lower the lower value
	 * @param higher the higher value
	 * @param index the index out of bound
	 */
	public static void failOnSmaller(int lower, int higher, int index)
	{
		if (lower < higher)
		{
			throw new StringIndexOutOfBoundsException(index);
		}
	}

	/**
	 * Throws a string out of bounds exception when the provided index is negative
	 * @param index the index value
	 */
	public static void failOnNegative(int index)
	{
		failOnSmaller(index, 0, index);
	}
}
