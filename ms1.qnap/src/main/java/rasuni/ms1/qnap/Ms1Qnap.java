package rasuni.ms1.qnap;

import org.pcollections.ConsPStack;
import rasuni.java.util.Arrays;
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
		MusicCollector.run(ConsPStack.from(Arrays.asList("\\\\MusikServer\\Musik", "\\\\qnap\\music", "\\\\qnap\\Qmultimedia")), "ms1qnap", "\\\\MusikServer\\Musik", true);
	}
}
