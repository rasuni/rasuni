package rasuni.taskqueue;

import java.util.List;

/**
 * Bound statement
 *
 */
public interface IStatement
{

	/**
	 * Get integer column value
	 * @param column the column index
	 * @return the column value
	 */
	int columnInt(int column);

	/**
	 * Fetch next data
	 * @return true if data exist
	 */
	boolean step();

	/**
	 * Get long column data
	 * @param column the column indexs
	 * @return the column value
	 */
	long columnLong(int column);

	/**
	 * Test for null value
	 * @param column the column index
	 * @return true if null, false otherwise
	 */
	boolean columnNull(int column);

	/**
	 * Get string column data
	 * @param column the column index
	 * @return the column value
	 */
	String columnString(int column);

	/**
	 * Rebind the statement
	 * @param parameterValues the new parameter values
	 */
	void rebind(List<IValue> parameterValues);
}
