package rasuni.ms1;

import rasuni.titan.TitanCollector;

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
		TitanCollector.run(System.security, System.props, "ms1", true, new String[] { "\\\\MUSIKSERVER", "Musik" });
	}
}
