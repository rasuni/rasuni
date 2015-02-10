package rasuni.java.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the class {@link SystemProperties}.
 */
public final class SystemPropertiesTest
{
	/**
	 * Test the method {@link SystemProperties#get(String)}
	 */
	@SuppressWarnings("static-method")
	@Test
	public void get()
	{
		Assert.assertNull(SystemProperties.get("\0"));
	}

	/**
	 * Test for full coverage
	 */
	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		ClassTest.construct(SystemProperties.class);
	}
}
