package ms1.music;

import java.util.Arrays;
import org.pcollections.ConsPStack;
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
		MusicCollector.run(ConsPStack.from(Arrays.asList("\\\\MusikServer\\Musik", "\\\\qnap\\music")), "ms1music", "\\\\MusikServer\\Musik", true);
	}
}
