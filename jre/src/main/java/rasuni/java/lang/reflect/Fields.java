package rasuni.java.lang.reflect;

import java.lang.reflect.Field;

/**
 * Fields utilities
 */
public final class Fields
{
	/**
	 * Get a a field value
	 * @param <T> the field type
	 * @param field the field
	 * @param object the object from where to read
	 * @return the field value
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Field field, Object object)
	{
		try
		{
			return (T) field.get(object);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
