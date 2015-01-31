package rasuni.functional;

/**
 *An expression with two parameters
 *
 * @param <R> the return type
 * @param <P1> the first parameter type
 * @param <P2> the second parameter type
 */
public interface IExpression2<R, P1, P2>
{
	/**
	 * Apply the expression to the parameters
	 * @param parameter1 the first parameter
	 * @param parameter2 the second parameter
	 * @return the return value
	 */
	R apply (P1 parameter1, P2 parameter2);
}
