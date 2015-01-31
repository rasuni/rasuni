package rasuni.functional;

/**
 * procedure closure
 *
 * @param <T> the parameter type
 */
public interface IConsumer<T>
{

	/**
	 * Execute the code
	 * @param parameter the actual parameter
	 */
	void accept(T parameter);
}
