package rasuni.taskqueue;

/**
 * A table definition
 * @author Ralph Sigrist
 *
 */
public interface ITableDef
{

	/**
	 * Return all columns
	 * @return all columns
	 */
	Iterable<IColumnDef> getColumns();

	/**
	 * @return all indexes
	 */
	Iterable<IIndex> getIndexes();

	/**
	 * Return the table name
	 * @return the table name
	 */
	String getName();
}