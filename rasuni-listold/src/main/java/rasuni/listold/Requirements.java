package rasuni.listold;

/**
 * Requirements for generating commands
 *
 */
public final class Requirements
{

	private long _lastAccessTime;
	private int _requiredCount;

	/**
	 * Constructor
	 * @param lastAccessTime last access time
	 * @param requiredCount required count
	 */
	public Requirements(long lastAccessTime, int requiredCount)
	{
		_lastAccessTime = lastAccessTime;
		_requiredCount = requiredCount;
	}

	/**
	 * Checks if requirements are fulfilled
	 * @param lastAccessTime new last access time
	 * @param scanCount the scan count
	 * @return true if fulfilled
	 */
	public boolean fulfilled(long lastAccessTime, int scanCount)
	{
		return (lastAccessTime != _lastAccessTime) || (_requiredCount <= scanCount);
	}
}
