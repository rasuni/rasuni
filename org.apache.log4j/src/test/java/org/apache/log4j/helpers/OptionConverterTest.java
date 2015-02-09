package org.apache.log4j.helpers;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the class {@link OptionConverter}
 */
public final class OptionConverterTest
{
	/**
	 * Test the method {@link OptionConverter#getSystemProperty(String, String, boolean, boolean, java.io.PrintStream)}
	 */
	@SuppressWarnings("static-method")
	@Test
	public void getSystemProperty()
	{
		Assert.assertNull(OptionConverter.getSystemProperty("\0", null, false, false, null));
	}

	/**
	 * Test the method {@link OptionConverter#getSystemProperty(String, String, boolean, boolean, java.io.PrintStream)}. Failure
	 */
	@SuppressWarnings("static-method")
	@Test
	public void getSystemPropertyFailureDefault()
	{
		Assert.assertNull(OptionConverter.getSystemProperty(null, null, false, false, null));
	}
}
