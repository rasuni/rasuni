package rasuni.taskqueue;

/**
 * Handler result
 *
 */
public final class HandlerResult
{

	private boolean _isNext;
	private boolean _isRequeue;
	
	/**
	 * Constructor
	 * @param isNext true if next iteration is requered
	 * @param isRequeue true if requeue is required
	 */
	public HandlerResult (boolean isNext, boolean isRequeue) {
		_isNext = isNext;
		_isRequeue = isRequeue;
	}
	
	/**
	 * return true if next iteration is required
	 * @return true if next iteration is required
	 */
	public boolean isNext()
	{
		return _isNext;
	}

	/**
	 * return true if requeue is required
	 * @return true if re-queue is required
	 */
	public boolean isRequeue()
	{
		return _isRequeue;
	}
}
