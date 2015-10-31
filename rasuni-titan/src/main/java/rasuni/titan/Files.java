package rasuni.titan;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * File utilities
 *
 */
public final class Files
{
	/**
	 * Determine the directory entry edge
	 *
	 * @param currentEntry
	 *            the file system object
	 * @return the edge representing the directory entry
	 */
	static Edge getDirectoryEntryEdge(Vertex currentEntry)
	{
		return TitanCollector.getSingleIncoming(currentEntry, "directoryEntry");
	}
}
