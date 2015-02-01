package rasuni.java.io;

import java.io.File;

/**
 * Utilities for files
 *
 */
public class Files
{
	private Files()
	{
		// disallow construction
	}

	/**
	 * Constructs a file object by setting the provided parent and using the child path of the provided file object
	 * @param parent the parent
	 * @param childPath the file object to extract the child path from
	 * @return the new file object
	 */
	public static File setParent(String parent, File childPath)
	{
		return new File(parent, childPath.getPath());
	}
}
