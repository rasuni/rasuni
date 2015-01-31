package rasuni.filesystemscanner;

import rasuni.taskqueue.ColumnValue;

/**
 * @author Ralph Sigrist
 *
 */
public interface IFileProcessingContext
{

	/**
	 * Create or update associated file object
	 * @param table the associated table
	 * @param columnValue the new column value
	 */
	void createOrUpdate(String table, ColumnValue<?> columnValue);
}
