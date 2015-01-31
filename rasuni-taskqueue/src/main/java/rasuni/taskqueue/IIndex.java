package rasuni.taskqueue;

/**
 * @author Ralph Sigrist
 * Index definition
 */
public interface IIndex
{
	/**
	 * Construct the SQL string
	 * @param tableName the table name
	 * @return the sql string
	 */
	String getCreateSql(String tableName);
}