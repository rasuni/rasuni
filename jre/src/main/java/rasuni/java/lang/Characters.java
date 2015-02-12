package rasuni.java.lang;

/**
 * Character utilities
 *
 */
public final class Characters
{
	private Characters()
	{
		// disallow construction
	}

	/**
	 * Determine whether the character at the given position is a non whitespace character
	 * @param value the character array
	 * @param position the position
	 * @return true if the specified character is a non whitespace character, false otherwise
	 */
	public static boolean isNonWhiteSpace(char[] value, int position)
	{
		return ' ' < value[position];
	}
}
