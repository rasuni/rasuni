package rasuni.java.lang;

import org.junit.Assert;
import org.junit.Test;
import rasuni.test.Classes;

/**
 * @Test case for the class {@link Characters}
 */
public class CharactersTest
{
	private final char[] ws = new char[] { '\000' };

	private final char[] nonws = new char[] { '!' };

	/**
	 * Test the method [{@link Characters#isNonWhiteSpace(char[], int)}. White space
	 */
	@Test
	public void isNonWhiteSpace()
	{
		Assert.assertFalse(Characters.isNonWhiteSpace(ws, 0));
	}

	/**
	 * Test the method [{@link Characters#isNonWhiteSpace(char[], int)}. Non white space
	 */
	@Test
	public void isNonWhiteSpaceNonWs()
	{
		Assert.assertTrue(Characters.isNonWhiteSpace(nonws, 0));
	}

	/**
	 * Test full coverage
	 */
	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		Classes.construct(Characters.class);
	}
}
