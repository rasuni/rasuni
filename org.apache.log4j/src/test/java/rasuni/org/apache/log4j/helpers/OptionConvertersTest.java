package rasuni.org.apache.log4j.helpers;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import rasuni.java.util.NotFound;
import rasuni.mock.MockTestCase;
import rasuni.test.Classes;

/**
 * Test case for the class {@link OptionConverters}
 */
public final class OptionConvertersTest extends MockTestCase
{
	interface IPrintStream
	{
		void println(String x);
	}

	private final IPrintStream _printStream = createStrictMock(IPrintStream.class);

	private final Properties _properties = new Properties();

	/**
	 * Test the method
	 * {@link OptionConverters#getSystemProperty(String, SecurityManager, java.util.Properties, String, boolean, boolean, java.io.PrintStream)}
	 */
	@Test
	public void getSystemProperty()
	{
		replay(() ->
		{
			Assert.assertNull(OptionConverters.getSystemProperty("\0", null, new Properties(), null, false, false, null));
		});
	}

	/**
	 * Test the method
	 * {@link OptionConverters#getSystemProperty(String, SecurityManager, java.util.Properties, String, boolean, boolean, java.io.PrintStream)}
	 * . Failure
	 */
	@Test
	public void getSystemPropertyFailureDefault()
	{
		_printStream.println("log4j: Was not allowed to read system property \"null\".");
		replay(() ->
		{
			Assert.assertNull(OptionConverters.getSystemProperty(null, null, null, null, true, false, new PrintStream(new OutputStream()
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
		Classes.construct(OptionConverters.class);
	}

	/**
	 * Test the method {@link OptionConverters#toBoolean(String, boolean)}. Null
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public void toBooleanNull()
	{
		Assert.assertFalse(OptionConverters.toBoolean(null, false));
	}

	/**
	 * Test the method {@link OptionConverters#toBoolean(String, boolean)}.
	 * Empty
	 */
	@SuppressWarnings("static-method")
	@Test
	public void toBooleanEmpty()
	{
		Assert.assertFalse(OptionConverters.toBoolean("", false));
	}

	/**
	 * Test the method {@link OptionConverters#toBoolean(String, boolean)}.
	 * Whitespace
	 */
	@SuppressWarnings("static-method")
	@Test
	public void toBooleanWhitespace()
	{
		Assert.assertFalse(OptionConverters.toBoolean(" ", false));
	}

	/**
	 * Test the method {@link OptionConverters#toBoolean(String, boolean)}. Non
	 * white space
	 */
	@SuppressWarnings("static-method")
	@Test
	public void toBooleanNonWhitespace()
	{
		Assert.assertFalse(OptionConverters.toBoolean("!", false));
	}

	/**
	 * Test the method {@link OptionConverters#toBoolean(String, boolean)}. Non
	 * white space at end
	 */
	@SuppressWarnings("static-method")
	@Test
	public void toBooleanWhitespaceAtEnd()
	{
		Assert.assertFalse(OptionConverters.toBoolean("! ", false));
	}

	/**
	 * Test parse property value
	 *
	 * @throws NotFound
	 *             not expected
	 */
	@Test
	public void toBooleanProps() throws NotFound
	{
		_properties.put("", "");
		Assert.assertTrue(OptionConverters.toBoolean(_properties, 0, 0, ""));
	}
}
