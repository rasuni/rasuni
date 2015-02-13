package rasuni.java.lang;

import rasuni.java.lang.reflect.Classes;

/**
 * String utilities
 */
public final class Strings
{
	private Strings()
	{
		// disallow construction
	}

	private static final IFunction VALUE = Classes.getter(String.class, "value");

	private static boolean startsWith(String prefix, IOffset offset, char[] value)
	{
		char[] pa = VALUE.apply(prefix);
		int pc = pa.length;
		// Note: toffset might be near -1>>>1.
		if (offset.apply(pc) > value.length)
		{
			return false;
		}
		else
		{
			int po = 0;
			for (;;)
			{
				if (po == pc)
				{
					return true;
				}
				if (value[offset.apply(po)] != pa[po])
				{
					return false;
				}
				po++;
			}
		}
	}

	/**
	 * Tests if the substring of the provided string  value beginning at the
	 * specified index starts with the specified prefix.
	 * @param   toffset   where to begin looking in this string.
	 * @param   prefix    the prefix.
	 * @param value the provided value
	 *
	 * @return  {@code true} if the character sequence represented by the
	 *          argument is a prefix of the substring of this object starting
	 *          at index {@code toffset}; {@code false} otherwise.
	 *          The result is {@code false} if {@code toffset} is
	 *          negative or greater than the length of this
	 *          {@code String} object; otherwise the result is the same
	 *          as the result of the expression
	 *          <pre>
	 *          this.substring(toffset).startsWith(prefix)
	 *          </pre>
	 */
	public static boolean startsWith(int toffset, String prefix, char[] value)
	{
		return toffset >= 0 && startsWith(prefix, pos -> pos + toffset, value);
	}

	/**
	 * Tests if this string starts with the specified prefix.
	 *
	 * @param   prefix   the prefix.
	 * @param string the string
	 * @return  {@code true} if the character sequence represented by the
	 *          argument is a prefix of the character sequence represented by
	 *          this string; {@code false} otherwise.
	 *          Note also that {@code true} will be returned if the
	 *          argument is an empty string or is equal to this
	 *          {@code String} object as determined by the
	 *          {@link #equals(Object)} method.
	 * @since   1. 0
	 */
	public static boolean startsWith(String prefix, String string)
	{
		return startsWith(prefix, pos -> pos, VALUE.apply(string));
	}
}
