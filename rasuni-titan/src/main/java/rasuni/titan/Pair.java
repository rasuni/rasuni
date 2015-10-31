package rasuni.titan;

/**
 * A pair
 *
 * @param <T1>
 *            the first type
 * @param <T2>
 *            the second type
 */
final class Pair<T1, T2>
{
	final T1 _first;

	final T2 _second;

	/**
	 * Constructor
	 *
	 * @param first
	 *            the first value
	 * @param second
	 *            the second value
	 */
	public Pair(T1 first, T2 second)
	{
		_first = first;
		_second = second;
	}
}
