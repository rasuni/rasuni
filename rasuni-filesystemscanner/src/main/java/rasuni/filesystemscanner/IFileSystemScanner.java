package rasuni.filesystemscanner;

import rasuni.taskqueue.IColumnValue;
import rasuni.taskqueue.Column;
import java.io.File;

/**
 * File system scanner
 *
 */
public interface IFileSystemScanner
{


	/**
	 * Return the minimal value of a column from a table
	 * @param <T>
	 * @param column the column
	 * @param table the table
	 * @return return the minimal value
	 */
	<T> T getMin(Column<T> column, String table);

	/**
	 * Select file system objects
	 * @param table the join table
	 * @param columnValue the where filter
	 * @return iterable of all file objects
	 */
	Iterable<File> select(String table, IColumnValue columnValue);

	/**
	 * Delete associated file system objects
	 * @param table the join table
	 * @param criteria the where filter
	 */
	void delete(String table, IColumnValue criteria);
}
