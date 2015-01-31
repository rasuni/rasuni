package rasuni.taskqueue;

import rasuni.lang.Value;



/**
 *A table column
 *
 * @param <T> the type of the column
 */
public final class Column<T> extends Value
{

	private String _name;
	private IType<T> _type;
	
	/**
	 * Constructor
	 * @param name the column name
	 * @param type the column type
	 */
	public Column (String name, IType<T> type) {
		_name = name;
		_type = type;
	}

	/**
	 * Return  the column name
	 * @return the column name
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Visit the type
	 * @param <VT> the visitor result type
	 * @param visitor the visitor to apply
	 * @return visitor result
	 */
	public <VT> VT visitType(ITypeVisitor<VT> visitor)
	{
		return _type.visit (visitor);
	}
	
	/**
	 * Factory for text columns
	 * @param name column name
	 * @return the column
	 */
	public static Column<String> text (String name)
	{
		return new Column<> (name, IType.STRING);
	}
	
	 
}
