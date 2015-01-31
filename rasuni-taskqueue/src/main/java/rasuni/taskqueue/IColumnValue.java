package rasuni.taskqueue;

/**
 * @author Ralph Sigrist
 * Column value
 */
public interface IColumnValue
{
	/**
	 * Return the column name
	 * @return the column name
	 */
	public abstract String getColumnName();

	/**
	 * Visit the column value
	 * @param visitor the visitor
	 */
	public abstract void visitValue(IValueVisitor visitor);
}