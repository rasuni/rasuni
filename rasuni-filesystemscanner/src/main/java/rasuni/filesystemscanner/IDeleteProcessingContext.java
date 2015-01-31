package rasuni.filesystemscanner;

/**
 * Context for file delete 
 *
 */
public interface IDeleteProcessingContext
{

	/**
	 * delete from associated record exists
	 * @param table other table
	 */
	void delete(String table);
}
