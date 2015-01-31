package rasuni.taskqueue;

/**
 * A Visitor for values
 *
 */
public interface IValueVisitor
{

	/**
	 * a text value
	 * @param value the value
	 */
	void text(String value);

	/**
	 * a null value
	 */
	void nullValue();

	/**
	 * an long value
	 * @param value the value
	 */
	void longValue(long value);
	
	/**
	 * an integer value
	 * @param value the value
	 */
	void integer(int value);
}
