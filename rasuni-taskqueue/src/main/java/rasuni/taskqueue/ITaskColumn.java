package rasuni.taskqueue;

/**
 * @author Ralph Sigrist
 * The optional task table column
 */
public interface ITaskColumn
{


	/**
	 * Return the name of the column
	 * @return the column name
	 */
	String getColumnName();

	/**
	 * Return the root value
	 * @return the root value
	 */
	String getValue();

	/**
	 * Return the column constraint
	 * @return the column constraint
	 */
	String getConstraint();
}
