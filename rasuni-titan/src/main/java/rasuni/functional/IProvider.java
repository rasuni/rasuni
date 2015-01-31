package rasuni.functional;

/**
 * A provider
 *
 * @param <T>
 *            type of the provided value
 */
public interface IProvider<T>
{
	/**
	 * Provide a value
	 * 
	 * @return the provided value
	 */
	T provide();
}
