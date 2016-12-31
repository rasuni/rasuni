package rasuni.filesystemscanner.impl;

import org.junit.Assert;
import org.junit.Test;
import rasuni.test.Classes;

public class CheckTest
{
	@SuppressWarnings("static-method")
	@Test
	public void expect()
	{
		try
		{
			Check.expect(false);
			Assert.fail();
		}
		catch (CheckException e)
		{
			// success
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void expectSuccess()
	{
		Check.expect(true);
	}

	@SuppressWarnings("static-method")
	@Test
	public void coverage()
	{
		Classes.construct(Check.class);
	}

	@SuppressWarnings("static-method")
	@Test
	public void fail()
	{
		try
		{
			Check.fail();
			Assert.fail();
		}
		catch (CheckException e)
		{
			// success
		}
	}
}
