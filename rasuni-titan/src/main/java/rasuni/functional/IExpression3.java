package rasuni.functional;


/**
 * An expression with 3 parameters
 *
 * @param <R> the result type
 * @param <P1> the type of the first parameter
 * @param <P2> the type of the second parameter
 * @param <P3> the type of the third parameter
 */
public interface IExpression3<R, P1, P2, P3>
{

	/**
	 * Apply the expression
	 * @param parameter1  the first parameter
	 * @param parameter2 the seccond parameter
	 * @param parameter3 the third parameter
	 * @return the result
	 */
	R apply(P1 parameter1, P2 parameter2, P3 parameter3);
}
