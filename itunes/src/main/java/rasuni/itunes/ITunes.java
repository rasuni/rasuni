package rasuni.itunes;

import org.pcollections.ConsPStack;
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
		MusicCollector.run(ConsPStack.singleton("D:\\ITunes\\Music"), "itunes", null, false);
	}
}
