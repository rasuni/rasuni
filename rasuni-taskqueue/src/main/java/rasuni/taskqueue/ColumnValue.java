package rasuni.taskqueue;




/**
 * a value for a column
 * @param <T> the value type
 *
 */
public final class ColumnValue<T> implements IColumnValue
{

	private T _value;
	private Column<T> _column;

	/**
	 * Constructor
	 * @param columnName the column name
	 * @param value the column value
	 */
	public ColumnValue(Column<T> columnName, T value)
	{
		_column = columnName;
		_value = value;
		
		
	}

	/* (non-Javadoc)
	 * @see rasuni.taskqueue.IColumnValue#getColumnName()
	 */
	@Override
	public String getColumnName()
	{
		return _column.getName();
	}

	/* (non-Javadoc)
	 * @see rasuni.taskqueue.IColumnValue#visitValue(rasuni.taskqueue.IValueVisitor)
	 */
	@Override
	public void visitValue(final IValueVisitor visitor)
	{
		if (_value == null) {
			visitor.nullValue();
		}
		else {
			_column.visitType (new ITypeVisitor<Void> () {
	
				@Override
				public Void integer()
				{
					visitor.integer((Integer) _value);
					return null;
				}
	
				@Override
				public Void longType()
				{
					visitor.longValue((Long) _value);
					return null;
				}
	
				@Override
				public Void string()
				{
					visitor.text((String) _value);
					return null;
				}});
		}
	}

	/**
	 * Create a column value from a long
	 * @param <T> the type
	 * @param column the column name
	 * @param value the long value
	 * @return the column value
	 */
	public static <T> IColumnValue create(Column<T> column, T value)
	{
		return new ColumnValue<>(column, value);
	}
}
