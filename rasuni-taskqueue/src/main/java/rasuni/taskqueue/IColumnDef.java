package rasuni.taskqueue;




/**
 * A table column 
 */
public interface IColumnDef
{

	/**
	 * Visit the column
	 * @param visitor the visitor
	 */
	void visit(IColumnVisitor visitor);
}
