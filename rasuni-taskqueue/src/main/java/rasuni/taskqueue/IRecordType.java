package rasuni.taskqueue;



/**
 * Maping from column to id
 *
 */
public interface IRecordType
{
	/**
	 * Get the column position
	 * @param column the column 
	 * @return the column position
	 */
	int getId(Column<?> column);
}
