package rasuni.musicdb;

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
		TitanCollector.run(System.security, System.props, "music", true, new String[] { "\\\\qnap", "music" });
	}
}
