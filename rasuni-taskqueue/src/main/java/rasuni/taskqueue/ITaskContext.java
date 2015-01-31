package rasuni.taskqueue;

/**
 * @author Ralph Sigrist
 * Task context interface 
 */
public interface ITaskContext
{


	/**
	 * Execute a sql statement with parameters
	 * @param string the sql string
	 * @param asList the parameter values
	 */
	void exec(String string, Iterable<IValue> asList);

	/**
	 * Insert row into table
	 * @param table the table name
	 * @param asList the column values
	 * @return the identity id of the new row
	 */
	long insert(String table, Iterable<IColumnValue> asList);

	/**
	 * Select query
	 * @param asList the columns
	 * @param table the table
	 * @param string
	 * @param asList2 the values
	 * @param b
	 * @param object
	 * @return the rows
	 */
	Iterable<IRow> select(Iterable<Column<?>> asList, String table, String string, Iterable<IValue> asList2, boolean b, String object);

	/**
	 * Select at most one row
	 * @param asList the columns
	 * @param tableFilesystemobject the table name
	 * @param string join source
	 * @param asList2 the values
	 * @param object where expression
	 * @return the row
	 */
	IRow  selectAtMostOne(Iterable<Column<?>> asList, String tableFilesystemobject, String string, Iterable<IValue> asList2, String object);

	/**
	 * Select one row
	 * @param asList the colums
	 * @param object the join source
	 * @param object2 the where expression
	 * @param valueIterableAdapter the values
	 * @return the row
	 */
	IRow selectOne(Iterable<Column<?>> asList, String object, String object2, Iterable<IValue> valueIterableAdapter);

	/**
	 * Enqueue a task
	 * @param taskKind the task kind
	 * @param foreignId the foreign id (optional)
	 * @return the id of the new task
	 */
	long addTask (int taskKind, String foreignId);

	/**
	 * Delete a row
	 * @param table the table
	 * @param idName the id column name
	 * @param taskid the id value
	 */
	void delete(String table, String idName, IValue taskid);

	/**
	 * Check row existence
	 * @param table the table
	 * @param condition the condition
	 * @return true if row exists
	 */
	boolean exists(String table, Iterable<IColumnValue> condition);

	
	
}
