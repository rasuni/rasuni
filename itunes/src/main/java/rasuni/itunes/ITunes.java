package rasuni.itunes;

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
		MusicCollector.run("D:\\ITunes", "itunes", null, false);
		//MusicCollector.run(ConsPStacks.singleton("D:\\ITunes\\Music"), "itunes", null, false);
	}
}
