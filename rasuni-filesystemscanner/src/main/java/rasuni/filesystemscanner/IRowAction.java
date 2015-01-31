package rasuni.filesystemscanner;

import rasuni.taskqueue.IRow;



/**
 * An action for a row
 * @param <T> result type
 *
 */
public interface IRowAction<T>
{

	/**
	 * Execute the action
	 * @param row the row
	 * @return the result
	 */
	T execute(IRow row);
}
