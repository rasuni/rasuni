package rasuni.titan;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import java.io.File;
import rasuni.check.Assert;
import rasuni.graph.Key;

/**
 * File utilities
 *
 */
public final class Files
{
	/**
	 * The name key
	 */
	public final static Key<String> NAME = TitanCollector.string("name");

	/**
	 * The directory entry label
	 */
	public final static String DIRECTORY_ENTRY = "directoryEntry";

	/**
	 * Delete a file and log the deletion
	 *
	 * @param file
	 *            the file to delete
	 */
	public static void delete(File file)
	{
		TitanCollector.log(new String[] { "  deleting" }, System.out);
		Assert.expect(file.delete());
	}

	/**
	 * Add a directory entry to a directory vertex
	 *
	 * @param directory
	 *            the directory vertex
	 * @param entry
	 *            the directory entry vertex
	 * @param name
	 *            the entry name
	 */
	public static void addDirectoryEntry(Vertex directory, Vertex entry, String name)
	{
		TitanCollector.set(directory.addEdge(DIRECTORY_ENTRY, entry), NAME, name);
	}

	/**
	 * Determine the directory entry edge
	 *
	 * @param currentEntry
	 *            the file system object
	 * @return the edge representing the directory entry
	 */
	public static Edge getDirectoryEntryEdge(Vertex currentEntry)
	{
		return TitanCollector.getSingleIncoming(currentEntry, DIRECTORY_ENTRY);
	}
}
