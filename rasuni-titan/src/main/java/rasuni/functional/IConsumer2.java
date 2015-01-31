package rasuni.functional;

/**
 * Consumer with two parameters
 *
 * @param <T1> type of the first parameter
 * @param <T2> type of the second parameter
 */
public interface IConsumer2<T1, T2>
{
	/**
	 * consume the parameters
	 * @param p1 the fist parameter
	 * @param p2 the second parameter
	 */
	void accept(T1 p1, T2 p2);
}
