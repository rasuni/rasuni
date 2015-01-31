package rasuni.musicdb;

import java.util.Arrays;
import java.util.LinkedList;
import rasuni.titan.TitanCollector;

/**
 * Qnap collection
 */
public class Qnap // NO_UCD (unused code)
{
	/**
	 * The entry point
	 */
	public static void main_deprecated()
	{
		LinkedList<Iterable<String>> roots = new LinkedList<>();
		roots.add(Arrays.asList("\\\\qnap", "Qmultimedia"));
		roots.add(Arrays.asList("\\\\qnap", "music"));
		TitanCollector.run("qnap", roots, true, Arrays.asList("\\\\qnap", "Qmultimedia"));
	}
}
