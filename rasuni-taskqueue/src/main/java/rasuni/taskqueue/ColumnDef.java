package rasuni.taskqueue;





/**
 * A table column definition
 * @author Ralph Sigrist
 *
 */
public final class ColumnDef implements IColumnDef
{
	
	private Column<?> _column;
	private String _constraint;
	/**
	 * the not null constraint
	 */
	public static final String NOT_NULL = "NOT NULL";
	/**
	 * the integer type name
	 */
	public static final String INTEGER = "INTEGER";
	/**
	 * the string type name
	 */
	public static final String STRING = "STRING";
	
	/**
	 * Constructor
	 * @param column the column consisting of type and name
	 * @param constraint the constraint
	 */
	public ColumnDef (Column<?> column, String constraint) 
	{
		_column = column;
		_constraint = constraint;
	
	}


	/**
	 * Apply a visitor
	 * @param visitor the visitor
	 */
	@Override
	public void visit(IColumnVisitor visitor)
	{
		visitor.definition (_column.getName(), _column.visitType (new ITypeVisitor<String> () {

			@Override
			public String integer()
			{
				return INTEGER;
			}

			@Override
			public String longType()
			{
				return "LONG";
			}

			@Override
			public String string()
			{
				return STRING;
			}}), _constraint);
	}
	
	/**
	 * Create a not null constrained column definition
	 * @param column the column
	 * @return the column definition
	 */
	public static ColumnDef notNull (Column<?> column) {
		return new ColumnDef (column, NOT_NULL);
	}

}
