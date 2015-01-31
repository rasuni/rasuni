package rasuni.taskqueue;

import rasuni.lang.Value;



/**
 * A result row
 *
 */
public final class Row extends Value implements IRow
{

	private Object[] _slots;
	private IRecordType _idsForColumn;

	/**
	 * Constructor
	 * @param slots the slots
	 * @param idsForColumn mapping from column to id
	 */
	public Row (Object[] slots, IRecordType idsForColumn) {
		_slots = slots;
		_idsForColumn = idsForColumn; 
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Column<T> column)
	{
		return (T) _slots [_idsForColumn.getId (column)];
	}
}
