package rasuni.taskqueue;



/**
 * A generic value
 *
 */
public interface IValue
{

	/**
	 * Apply a visitor
	 * @param visitor the visitor to apply
	 */
	void visit(IValueVisitor visitor);
}
