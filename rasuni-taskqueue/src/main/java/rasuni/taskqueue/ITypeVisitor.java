package rasuni.taskqueue;

/**
 * A type visitor
 * @param <T> the result type
 *
 */
public interface ITypeVisitor<T>
{

	/**
	 * Integer type
	 * @return an integer value
	 */
	T integer();

	/**
	 * Long type
	 * @return a long value
	 */
	T longType();

	/**
	 * String type
	 * @return a string value
	 */
	T string();
}
