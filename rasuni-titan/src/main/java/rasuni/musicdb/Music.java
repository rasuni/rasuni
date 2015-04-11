package rasuni.musicdb;

import org.apache.log4j.helpers.LogLog;
import rasuni.titan.TitanCollector;

/**
 * Recording collector entry
 */
public final class Music // NO_UCD (unused code)
{
	/**
	 * The main entry point
	 */
	public static void main_deprecated()
	{
		TitanCollector.run(System.security, System.props, LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "music", true, new String[] { "\\\\qnap", "music" });
	}
}
