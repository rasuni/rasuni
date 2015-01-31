package rasuni.taskqueue;

import java.util.Iterator;

/**
 * Adapter to iterable IValue
 *
 */
public final class ValueIterableAdapter implements Iterable<IValue>
{
	private final Iterable<IColumnValue> _columnValues;

	/**
	 * Constructor
	 * @param columnValues the column values
	 */
	public ValueIterableAdapter(Iterable<IColumnValue> columnValues)
	{
		_columnValues = columnValues;
	}

	@Override
	public Iterator<IValue> iterator()
	{
		return new Iterator<IValue>()
		{
			private Iterator<IColumnValue> _iterator = _columnValues.iterator();

			@Override
			public boolean hasNext()
			{
				return _iterator.hasNext();
			}

			@Override
			public IValue next()
			{
				final IColumnValue next = _iterator.next();
				return new IValue()
				{
					@Override
					public void visit(IValueVisitor visitor)
					{
						next.visitValue(visitor);
					}
				};
			}

			@Override
			public void remove()
			{
				throw new RuntimeException("not implemented!");
			}
		};
	}
}