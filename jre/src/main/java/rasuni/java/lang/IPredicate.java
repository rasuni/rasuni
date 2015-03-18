package rasuni.java.lang;

/**
 * A predicate with two integer arguments.
 */
public interface IPredicate
{
	/**
	 * Apply the predicate for the two arguments
	 * @param arg1 the first integer
	 * @param arg2 the second integer
	 * @return condition result
	 */
	boolean check(int arg1, int arg2);
}
