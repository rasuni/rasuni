package rasuni.qmultimedia;

import java.util.Properties;
import org.apache.log4j.helpers.LogLog;
import rasuni.titan.TitanCollector;

/**
 * @author Ralph Sigrist QMultimedia Library
 */
public final class QMultimedia // NO_UCD (unused code)
{
	private static void main(SecurityManager security, Properties properties)
	{
		TitanCollector.run(security, properties, LogLog.g_debugEnabled, LogLog.g_quietMode, System.out, "qmultimedia", true, new String[] { "\\\\qnap", "Qmultimedia" });
	}

	/**
	 * @param args
	 *            the main arguments (not used)
	 */
	public static void main(String[] args)
	{
		main(System.getSecurityManager(), System.getProperties());
	}
}
