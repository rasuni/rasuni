package rasuni.itunes;

import java.util.Arrays;
import java.util.LinkedList;
import rasuni.titan.TitanCollector;

/**
 * ITunes library
 *
 */
public final class ITunes // NO_UCD (unused code)
{
	/**
	 * @param args
	 *            the main arguments (not used)
	 */
	public static void main(String[] args)
	{
		LinkedList<Iterable<String>> paths = new LinkedList<>();
		paths.add(Arrays.asList("D:", "ITunes", "Music"));
		paths.add(Arrays.asList("D:", "playlists"));
		TitanCollector.run(System.security, System.props, "itunes", paths, false, Arrays.asList("D:"));
	}
}
