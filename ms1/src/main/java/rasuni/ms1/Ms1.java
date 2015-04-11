package rasuni.ms1;

import java.util.Properties;
import org.apache.log4j.helpers.LogLog;
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
		main(System.getSecurityManager(), System.getProperties());
	}

	/**
	 * The main entry point
	 *
	 * @param args
	 *            the arguments
	 */
	private static void main(SecurityManager security, Properties props)
	{
		TitanCollector.run(security, props, LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "ms1", true, new String[] { "\\\\MUSIKSERVER", "Musik" });
	}
}
