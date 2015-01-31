package rasuni.taskqueue;


/**
 * Interface for a data base application
 * @author Ralph Sigrist
 *
 */
public interface IDatabaseApplication
{

	/**
	 * Run the applicatuon
	 * @param connection the connection
	 * @param dbFileExists true if data base file exists
	 */
	void run(IConnection connection);
}
