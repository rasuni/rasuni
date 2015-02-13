package rasuni.java.lang.reflect;

import java.lang.reflect.Field;

/**
 * Class helpers
 */
public final class Classes
{
	/**
	 * Provide an accessible declared field with the specified name.
	 * @param cls the class
	 * @param name the field name
	 * @return the field
	 */
	public static Field getDeclaredField(Class<String> cls, String name)
	{
		try
		{
			Field result = cls.getDeclaredField(name);
			result.setAccessible(true);
			return result;
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}
}
