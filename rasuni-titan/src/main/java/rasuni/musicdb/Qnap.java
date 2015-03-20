package rasuni.musicdb;

import java.util.Arrays;
import java.util.LinkedList;
import org.apache.log4j.helpers.LogLog;
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
		TitanCollector.run(System.security, System.props, LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "qnap", roots, true, Arrays.asList("\\\\qnap", "Qmultimedia"));
	}
}
