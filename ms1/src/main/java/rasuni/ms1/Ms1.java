package rasuni.ms1;

import fj.data.Array;
import rasuni.titan.MusicCollector;

/**
 * MS1 recordings
 */
public class Ms1 // NO_UCD (unused code)
{
	/**
	 * The main entry point
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args)
	{
		MusicCollector.run(Array.array("\\\\MUSIKSERVER\\Musik"), "ms1", System.out, "\\\\MUSIKSERVER\\Musik", true);
	}
}
