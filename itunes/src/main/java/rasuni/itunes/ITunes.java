package rasuni.itunes;

import fj.data.Array;
import rasuni.titan.MusicCollector;

/**
 * ITunes library
 *
 */
public final class ITunes
{
	/**
	 * @param args
	 *            the main arguments (not used)
	 */
	public static void main(String[] args)
	{
		MusicCollector.run(Array.array("D:\\ITunes\\Music"), "itunes", System.out, null, false);
	}
}
