package rasuni.filesystemscanner;


/**
 * The root entry registry
 *
 */
public interface IRootRegistry
{

	/**
	 * Register root entry
	 * @param pathEntries the root file
	 */
	void register(Iterable<String> pathEntries);
}
