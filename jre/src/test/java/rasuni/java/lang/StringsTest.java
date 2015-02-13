package rasuni.java.lang;

import org.junit.Assert;
import org.junit.Test;
import rasuni.test.Classes;

/**
 * Test case for the class {@link Strings}
 *
 */
public class StringsTest
{
	private final char[] empty = new char[] {};

	private final char[] one = new char[] { '\001' };

	/**
	 * Test the method {@link Strings#startsWith(int, String, char[])}. Negative offset.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void startsWithNegativeOffset()
	{
		Assert.assertFalse(Strings.startsWith(-1, null, null));
	}

	/**
	 * Test the method {@link Strings#startsWith(int, String, char[])}. Empty string.
	 */
	@Test
	public void startWithEmptyString()
	{
		Assert.assertFalse(Strings.startsWith(0, "\000", empty));
	}

	/**
	 * Test the method {@link Strings#startsWith(int, String, char[])}. Empty prefix, empty string.
	 */
	@Test
	public void startWithEmptyPrefixEmptyString()
	{
		Assert.assertTrue(Strings.startsWith(0, "", empty));
	}

	/**
	 * Test the method {@link Strings#startsWith(int, String, char[])}. No prefix match
	 */
	@Test
	public void startWithNoMatch()
	{
		Assert.assertFalse(Strings.startsWith(0, "\000", one));
	}

	/**
	 * Test the method {@link Strings#startsWith(int, String, char[])}. No prefix match
	 */
	@Test
	public void startWithMatch()
	{
		Assert.assertTrue(Strings.startsWith(0, "\001", one));
	}

	/**
	 * Test the method {@link Strings#startsWith(int, String, char[])}. No prefix match
	 */
	@SuppressWarnings("static-method")
	@Test
	public void startWith()
	{
		Assert.assertTrue(Strings.startsWith("\001", "\001"));
	}

	/**
	 * Reach 100% coverage
	 */
	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		Classes.construct(Strings.class);
	}
}
