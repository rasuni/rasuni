package rasuni.ms1;

import org.pcollections.ConsPStack;
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
		MusicCollector.run(ConsPStack.singleton("\\\\MUSIKSERVER\\Musik"), "ms1", "\\\\MUSIKSERVER\\Musik", true);
	}
}
