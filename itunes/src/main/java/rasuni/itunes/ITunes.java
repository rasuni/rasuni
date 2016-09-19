package rasuni.itunes;

import rasuni.org.pcollections.ConsPStacks;
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
		MusicCollector.run(ConsPStacks.singleton("D:\\ITunes\\Music"), "itunes", null, false);
	}
}
