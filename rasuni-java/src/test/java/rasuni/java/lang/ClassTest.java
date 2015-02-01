package rasuni.java.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Test utilities for classes
 *
 */
public class ClassTest
{
	/**
	 * Construct an instance of the specified class
	 * @param cls the class
	 */
	public static void construct(Class<?> cls)
	{
		try
		{
			Constructor<?> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			try
			{
				constructor.newInstance();
			}
			catch (InstantiationException e)
			{
				throw new RuntimeException(e);
			}
			catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
			catch (IllegalArgumentException e)
			{
				throw new RuntimeException(e);
			}
			catch (InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
		catch (SecurityException e)
		{
			throw new RuntimeException(e);
		}
	}
}
