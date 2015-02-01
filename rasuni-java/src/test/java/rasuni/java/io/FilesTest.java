package rasuni.java.io;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import rasuni.java.lang.ClassTest;

/**
 * Test case for the class {@link Files}
 *
 */
public class FilesTest
{
	private final File file = new File("");

	/**
	 * Test the method {@link Files#setParent(String, File)}
	 */
	@Test
	public void setParent()
	{
		Assert.assertEquals(Files.setParent(null, file), file);
	}

	/**
	 * Reach full test coverage
	 */
	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		ClassTest.construct(Files.class);
	}
}
