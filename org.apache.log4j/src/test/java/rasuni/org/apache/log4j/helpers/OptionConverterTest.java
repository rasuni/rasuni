package rasuni.org.apache.log4j.helpers;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import rasuni.mock.MockTestCase;
import rasuni.test.Classes;

/**
 * Test case for the class {@link OptionConverter}
 */
public final class OptionConverterTest extends MockTestCase
{
	interface IPrintStream
	{
		void println(String x);
	}

	private final IPrintStream _printStream = createStrictMock(IPrintStream.class);

	/**
	 * Test the method {@link OptionConverter#getSystemProperty(String, SecurityManager, java.util.Properties, String, boolean, boolean, java.io.PrintStream)}
	 */
	@Test
	public void getSystemProperty()
	{
		replay(() ->
		{
			Assert.assertNull(OptionConverter.getSystemProperty("\0", null, new Properties(), null, false, false, null));
		});
	}

	/**
	 * Test the method {@link OptionConverter#getSystemProperty(String, SecurityManager, java.util.Properties, String, boolean, boolean, java.io.PrintStream)}. Failure
	 */
	@Test
	public void getSystemPropertyFailureDefault()
	{
		_printStream.println("log4j: Was not allowed to read system property \"null\".");
		replay(() ->
		{
			Assert.assertNull(OptionConverter.getSystemProperty(null, null, null, null, true, false, new PrintStream(new OutputStream()
			{
				@Override
				public void write(int b)
				{
					Assert.fail();
				}
			})
			{
				@Override
				public void println(String x)
				{
					_printStream.println(x);
				}
			}));
		});
	}

	/**
	 * full code coverage
	 */
	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		Classes.construct(OptionConverter.class);
	}
}
