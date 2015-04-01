package rasuni.java.lang;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import rasuni.java.lang.reflect.Classes;
import rasuni.java.lang.reflect.Constructors;

/**
 * String utilities
 */
public final class Strings
{
	private Strings()
	{
		// disallow construction
	}

	/**
	 * The value getter
	 */
	public static final IFunction<char[], String> VALUE = Classes.getter(String.class, "value");

	private static final Constructor<String> CONSTRUCTOR;
	static
	{
		CONSTRUCTOR = Classes.getConstructor(String.class, char[].class, boolean.class);
		CONSTRUCTOR.setAccessible(true);
	}

	static boolean compare(String string, IPredicate lengthMatches, IIntFunction offset, char[] value)
	{
		char[] pa = VALUE.apply(string);
		int pc = pa.length;
		if (lengthMatches.check(offset.apply(pc), value.length))
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
		else
		{
			return false;
		}
	}

	private static boolean startsWith(String prefix, IIntFunction offset, char[] value)
	{
		return compare(prefix, (l1, l2) -> l1 <= l2, offset, value);
	}

	/**
	 * Tests if the substring of the provided string value beginning at the
	 * specified index starts with the specified prefix.
	 *
	 * @param toffset
	 *            where to begin looking in this string.
	 * @param prefix
	 *            the prefix.
	 * @param value
	 *            the provided value
	 *
	 * @return {@code true} if the character sequence represented by the
	 *         argument is a prefix of the substring of this object starting at
	 *         index {@code toffset}; {@code false} otherwise. The result is
	 *         {@code false} if {@code toffset} is negative or greater than the
	 *         length of this {@code String} object; otherwise the result is the
	 *         same as the result of the expression
	 *
	 *         <pre>
	 * this.substring(toffset).startsWith(prefix)
	 * </pre>
	 */
	public static boolean startsWith(int toffset, String prefix, char[] value)
	{
		return toffset >= 0 && startsWith(prefix, pos -> pos + toffset, value);
	}

	/**
	 * Tests if this string starts with the specified prefix.
	 *
	 * @param prefix
	 *            the prefix.
	 * @param string
	 *            the string
	 * @return {@code true} if the character sequence represented by the
	 *         argument is a prefix of the character sequence represented by
	 *         this string; {@code false} otherwise. Note also that {@code true}
	 *         will be returned if the argument is an empty string or is equal
	 *         to this {@code String} object as determined by the
	 *         {@link #equals(Object)} method.
	 * @since 1. 0
	 */
	public static boolean startsWith(String prefix, String string)
	{
		return startsWith(prefix, Integers::identity, VALUE.apply(string));
	}

	/**
	 * Compares a string to the specified object. The result is {@code true} if
	 * and only if the argument is not {@code null} and is a {@code String}
	 * object that represents the same sequence of characters as this object.
	 *
	 * @param string
	 *            the string to compare
	 *
	 * @param anObject
	 *            The object to compare this {@code String} against
	 *
	 * @return {@code true} if the given object represents a {@code String}
	 *         equivalent to this string, {@code false} otherwise
	 *
	 */
	public static boolean equals(String string, Object anObject)
	{
		return anObject instanceof String && compare(string, (l1, l2) -> l1 == l2, Integers::identity, VALUE.apply((String) anObject));
	}

	/**
	 * Constructs a string based on the given character array consisting of a
	 * range therefore.
	 *
	 * @param value
	 *            the character array
	 *
	 * @param start
	 *            the start position
	 * @param len
	 *            the length
	 * @return return the constructed string
	 */
	public static String copyOfRange(char[] value, int start, final int len)
	{
		return Constructors.newInstance(CONSTRUCTOR, Arrays.copyOfRange(value, start, len), Boolean.TRUE);
	}
}
