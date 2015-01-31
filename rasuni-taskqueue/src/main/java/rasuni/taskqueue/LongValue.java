package rasuni.taskqueue;

import rasuni.lang.Value;




/**
 * A long value
 *
 */
public final class LongValue extends Value implements IValue
{
	
	private long _value;

	/**
	 * Constructor
	 * @param value the value
	 */
	public LongValue(long value)
	{
		_value = value;
		
	}

	@Override
	public void visit(IValueVisitor visitor)
	{
		visitor.longValue(_value);
	}
}
