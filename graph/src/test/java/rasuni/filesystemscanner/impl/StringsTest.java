package rasuni.filesystemscanner.impl;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import rasuni.test.Classes;

public class StringsTest
{
	@SuppressWarnings("static-method")
	@Test
	public void ctor()
	{
		Classes.construct(Strings.class);
	}

	@SuppressWarnings("static-method")
	@Test
	public void join()
	{
		Assert.assertEquals("", Strings.join(Arrays.asList("")));
	}

	@SuppressWarnings("static-method")
	@Test
	public void join2()
	{
		Assert.assertEquals(" ", Strings.join(Arrays.asList("", "")));
	}

	@SuppressWarnings("static-method")
	@Test
	public void join0()
	{
		Assert.assertEquals("", Strings.join(Arrays.asList()));
	}
}
