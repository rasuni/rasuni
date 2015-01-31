package rasuni.filesystemscanner;

import rasuni.taskqueue.IValue;
import rasuni.taskqueue.IValueVisitor;



/**
 * A text value
 */
public class Text implements IValue
{

	private String _value;

	/**
	 * Constructor
	 * @param value the value
	 */
	public Text(String value)
	{
		_value = value;
	}

	@Override
	public void visit(IValueVisitor visitor)
	{
		visitor.text(_value);
	}
}
