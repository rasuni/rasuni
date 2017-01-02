package rasuni.filesystemscanner.impl;

public final class Check
{
	private Check()
	{
	}

	public static void fail()
	{
		throw new CheckException();
	}

	private static void failIf(boolean check)
	{
		if (check)
		{
			throw new CheckException();
		}
	}

	public static void expect(boolean check)
	{
		failIf(!check);
	}
}
