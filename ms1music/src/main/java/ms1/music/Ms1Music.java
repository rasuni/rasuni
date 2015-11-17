package ms1.music;

import fj.data.Array;
import rasuni.titan.MusicCollector;

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
		MusicCollector.run(Array.array("\\\\MusikServer\\Musik", "\\\\qnap\\music"), "ms1music", System.out, "\\\\MusikServer\\Musik", true);
	}
}
