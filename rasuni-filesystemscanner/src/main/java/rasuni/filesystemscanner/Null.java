package rasuni.filesystemscanner;

import rasuni.taskqueue.IValue;
import rasuni.taskqueue.IValueVisitor;



/**
 * The null value
 *
 */
public final class Null implements IValue
{

	/**
	 * the one and only instance
	 */
	public static final Null INSTANCE = new Null ();
	
	private Null () 
	{
		// disallow construction 
	}

	@Override
	public void visit(IValueVisitor visitor)
	{
		visitor.nullValue();
	}
}
