package rasuni.taskqueue;

/**
 * Visitor for a column definition
 *
 */
public interface IColumnVisitor
{

	/**
	 * Visitor the column definition
	 * @param name the column name
	 * @param typeName the type name
	 * @param constraint the constraint
	 * @param indexName the index name
	 */
	void definition(String name, String typeName, String constraint);

	/**
	 * Visit a table constraint
	 * @param columnName the column name
	 * @param referencedTable the referenced table
	 */
	void tableConstraint(String columnName, String referencedTable);
	
}
