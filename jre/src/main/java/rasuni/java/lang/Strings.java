package rasuni.java.lang;

import java.lang.reflect.Field;
import rasuni.java.lang.reflect.Classes;
import rasuni.java.lang.reflect.Fields;

/**
 * String utilities
 */
public final class Strings
{
	private Strings()
	{
		// disallow construction
	}

	private static final Field VALUE = Classes.getDeclaredField(String.class, "value");

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
		if (toffset < 0)
		{
			return false;
		}
		else
		{
			char pa[] = (char[]) Fields.get(VALUE, prefix);
			int pc = pa.length;
			// Note: toffset might be near -1>>>1.
			if (toffset + pc > value.length)
			{
				return false;
			}
			else
			{
				int po = 0;
				final int length = pa.length;
				for (;;)
				{
					if (po == length)
					{
						return true;
					}
					if (value[toffset] != pa[po])
					{
						return false;
					}
					toffset++;
					po++;
				}
			}
		}
	}
}
