package rasuni.taskqueue;





/**
 * A column which references a row in a table
 *
 */
public final class Reference implements IColumnDef
{
	private boolean _required;
	private String _referencedTable;
	private String _name;

	/**
	 * Constructor
	 * @param name the column name
	 * @param required true if required
	 * @param referencedTable the referenced table
	 */
	public Reference (String name, boolean required, String referencedTable)
	{
		_name = name;
		_required = required;
		_referencedTable = referencedTable;
	}

	@Override
	public void visit(IColumnVisitor visitor)
	{
		visitor.definition(_name, ColumnDef.INTEGER, _required ? ColumnDef.NOT_NULL : null);
		visitor.tableConstraint (_name, _referencedTable);
	}
}
