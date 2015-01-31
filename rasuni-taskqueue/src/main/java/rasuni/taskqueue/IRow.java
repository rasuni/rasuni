package rasuni.taskqueue;




/**
 * A row in a result set
 *
 */
public interface IRow
{

	/**
	 * Get an integer column value
	 * @param <T> the column type
	 * @param column the column name
	 * @return the integer column value
	 */
	<T> T get(Column<T> column);

}
