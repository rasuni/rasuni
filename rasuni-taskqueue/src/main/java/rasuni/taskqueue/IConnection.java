package rasuni.taskqueue;



/**
 * A database connection
 * @author Ralph Sigrist
 *
 */
public interface IConnection
{

	/**
	 * Create a table
	 * @param name the table name
	 * @param columns the columns
	 * @param indexes the indexes
	 */
	void createTable(String name, Iterable<IColumnDef> columns, Iterable<IIndex> indexes);


	/**
	 * Add root task
	 * @return the task id
	 */
	long addRoot();



}
