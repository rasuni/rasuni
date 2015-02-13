package rasuni.java.lang.reflect;

import java.lang.reflect.Field;

/**
 * Fields utilities
 */
public final class Fields
{
	/**
	 * Get a a field value
	 * @param field the field
	 * @param object the object from where to read
	 * @return the field value
	 */
	public static Object get(Field field, Object object)
	{
		try
		{
			return field.get(object);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
