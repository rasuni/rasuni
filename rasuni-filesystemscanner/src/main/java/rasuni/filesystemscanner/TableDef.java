package rasuni.filesystemscanner;

import rasuni.taskqueue.IColumnDef;
import rasuni.taskqueue.IIndex;
import rasuni.taskqueue.ITableDef;

/**
 * @author Ralph Sigrist
 * A table definition
 */
public final class TableDef implements ITableDef
{

	private String _name;
	private Iterable<IColumnDef> _columns;
	private Iterable<IIndex> _indexes;
	
	/**
	 * Constructor
	 * @param name table name
	 * @param columns table column
	 * @param indexes table indexes
	 */
	public TableDef (String name, Iterable<IColumnDef> columns, Iterable<IIndex> indexes) {
		_name = name;
		_columns = columns;
		_indexes = indexes;
	}

	/**
	 * The table name
	 * @return the table name
	 */
	@Override
	public String getName()
	{
		return _name;
	}

	/**
	 * Return table columns
	 * @return the table columns
	 */
	@Override
	public Iterable<IColumnDef> getColumns()
	{
		return _columns;
	}

	/**
	 * Return the table indexes
	 * @return the table indexes
	 */
	@Override
	public Iterable<IIndex> getIndexes()
	{
		return _indexes;
	}
}
