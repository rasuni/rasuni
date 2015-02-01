package rasuni.java.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for the class {@link Arrays}
 */
public class ArraysTest
{
	private final Object[] array1 = new Object[] { null };

	/**
	 * Test for full coverage
	 */
	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		ClassTest.construct(Arrays.class);
	}

	/**
	 * Test the method {@link Arrays#setAt(Object[], int, Object)}. Last member
	 */
	@Test
	public void setAt()
	{
		Assert.assertArrayEquals(Arrays.setAt(array1, 0, null), array1);
	}
}
