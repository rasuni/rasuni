package rasuni.ms1.qnap;

import fj.data.Array;
import rasuni.titan.MusicCollector;

/**
 * Qnap collection
 */
public class Ms1Qnap // NO_UCD (unused code)
{
	/**
	 * The entry point
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args)
	{
		MusicCollector.run(Array.array("\\\\MusikServer\\Musik", "\\\\qnap\\music", "\\\\qnap\\Qmultimedia"), "ms1qnap", System.out, "\\\\MusikServer\\Musik", true);
	}
}
