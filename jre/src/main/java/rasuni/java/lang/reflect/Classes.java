package rasuni.java.lang.reflect;

import java.lang.reflect.Field;
import rasuni.java.lang.IFunction;

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
	public static IFunction getter(Class<?> cls, String name)
	{
		try
		{
			Field field = cls.getDeclaredField(name);
			field.setAccessible(true);
			return object -> Fields.get(field, object);
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}
}
