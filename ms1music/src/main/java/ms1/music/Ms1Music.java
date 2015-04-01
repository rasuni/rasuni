package ms1.music;

import java.util.Arrays;
import java.util.LinkedList;
import org.apache.log4j.helpers.LogLog;
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
		TitanCollector.run(System.getSecurityManager(), System.getProperties(), LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "ms1music", roots, true, Arrays.asList("\\\\qnap", "music"));
	}
}
