package rasuni.java.lang;

import java.lang.reflect.Array;

/**
 * Array utilities
 *
 */
public final class Arrays
{
	private Arrays()
	{
		// disallow construction
	}

	/**
	 * Produce an array based on the source array. The resulting array has all members the same as the source array, except the one member replaced with the given new value.
	 * @param <T> member type
	 * @param source the source array
	 * @param position the position
	 * @param newMemberValue the new member value
	 * @return the resulting array based on the source array but with the specified value at the specified position
	 */
	public static <T> T[] setAt(T[] source, int position, T newMemberValue)
	{
		int length = source.length;
		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(source.getClass().getComponentType(), length);
		System.arraycopy(source, 0, result, 0, position);
		result[position] = newMemberValue;
		int remaining = position + 1;
		System.arraycopy(source, remaining, result, remaining, length - remaining);
		return result;
	}
}
