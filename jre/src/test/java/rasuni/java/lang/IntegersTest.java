package rasuni.java.lang;

import org.junit.Assert;
import org.junit.Test;
import rasuni.test.Classes;

/**
 * Test case for the class {@link Integers}
 *
 */
public class IntegersTest
{
	/**
	 * Test the method {@link Integers#identity(int)}
	 */
	@SuppressWarnings("static-method")
	@Test
	public void identity()
	{
		Assert.assertEquals(0, Integers.identity(0));
	}

	/**
	 * Reach full coverage
	 */
	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		Classes.construct(Integers.class);
	}

	/**
	 * Test the method {@link Integers#isNegative(int)}
	 */
	@SuppressWarnings("static-method")
	@Test
	public void isNegative()
	{
		Assert.assertFalse(Integers.isNegative(0));
	}

	/**
	 * Test the method {@link Integers#isNegative(int)}. Negative
	 */
	@SuppressWarnings("static-method")
	@Test
	public void isNegativeTrue()
	{
		Assert.assertTrue(Integers.isNegative(-1));
	}
}
