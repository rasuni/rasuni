package rasuni.taskqueue;

/**
 * A database index
 *
 */
public final class Index implements IIndex
{
	private Iterable<String> _columns;
	private boolean _isUnique;

	/**
	 * Constructor
	 * @param isUnique true if is unique
	 * @param columns the columns
	 */
	public Index (boolean isUnique, Iterable<String> columns) {
		_isUnique = isUnique;
		_columns = columns;
	}

	/* (non-Javadoc)
	 * @see rasuni.taskqueue.IIndex#getCreateSql(java.lang.String)
	 */
	@Override
	public String getCreateSql(String tableName)
	{
		StringBuffer sql = new StringBuffer();
		sql.append ("CREATE ");
		if (_isUnique) {
			sql.append("UNIQUE ");
		}
		sql.append("INDEX ");
		sql.append (tableName);
		StringBuffer fields = new StringBuffer ();
		boolean needsComma = false;
		for (String column : _columns) {
			sql.append ("_");
			sql.append (column);
			if (needsComma) {
				fields.append(", ");
			}
			else {
				needsComma = true;
			}
			fields.append (column);
		}
		sql.append (" ON ");
		sql.append(tableName);
		sql.append (" (");
		sql.append(fields);
		sql.append(")");
		return sql.toString();
		//return ("CREATE UNIQUE INDEX " + tableName + "_" + entry.getKey() + " ON " + tableName + " (" + entry.getValue() + ")");
	}
}
