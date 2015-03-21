package rasuni.java.lang;

import org.junit.Assert;
import org.junit.Test;
import rasuni.test.Classes;

/**
 * Test case for the class {@link StringIndexOutOfBoundsExceptions}
 *
 */
public class StringIndexOutOfBoundsExceptionsTest
{
	/**
	 * Test the method {@link StringIndexOutOfBoundsExceptions#failOnSmaller(int, int, int)}
	 */
	@SuppressWarnings("static-method")
	@Test
	public void faiOnSmaller()
	{
		try
		{
			StringIndexOutOfBoundsExceptions.failOnSmaller(0, 1, 0);
			Assert.fail();
		}
		catch (StringIndexOutOfBoundsException e)
		{
			// succeed
		}
	}

	/**
	 * Test the method {@link StringIndexOutOfBoundsExceptions#failOnSmaller(int, int, int)}
	 */
	@SuppressWarnings("static-method")
	@Test
	public void failOnSmallerNoFail()
	{
		StringIndexOutOfBoundsExceptions.failOnSmaller(0, 0, 0);
	}

	/**
	 * Reach full coverage
	 */
	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		Classes.construct(StringIndexOutOfBoundsExceptions.class);
	}

	/**
	 *  Test the method {@link StringIndexOutOfBoundsExceptions#failOnNegative(int)}
	 */
	@SuppressWarnings("static-method")
	@Test
	public void failOnNegative()
	{
		StringIndexOutOfBoundsExceptions.failOnNegative(0);
	}
}
