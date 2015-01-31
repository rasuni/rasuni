package rasuni.musicdb;

import rasuni.titan.TitanCollector;

/**
 * @author Ralph Sigrist QMultimedia Library
 */
public final class QMultimedia // NO_UCD (unused code)
{
	/**
	 * @param args
	 *            the main arguments (not used)
	 */
	public static void main(String[] args)
	{
		TitanCollector.run("qmultimedia", true, new String[] { "\\\\qnap", "Qmultimedia" });
	}
}
