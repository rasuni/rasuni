package rasuni.musicdb;

import java.util.Arrays;
import java.util.LinkedList;
import rasuni.titan.TitanCollector;

/**
 * Qnap collection
 */
public class Ms1Music // NO_UCD (unused code)
{
	/**
	 * The entry point
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args)
	{
		LinkedList<Iterable<String>> roots = new LinkedList<>();
		roots.add(Arrays.asList("\\\\MusikServer", "Musik"));
		roots.add(Arrays.asList("\\\\qnap", "music"));
		TitanCollector.run("ms1music", roots, true, Arrays.asList("\\\\qnap", "music"));
	}
}
