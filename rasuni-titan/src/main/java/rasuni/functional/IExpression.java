package rasuni.functional;

/**
 * Expression
 *
 * @param <R>
 *            return type
 * @param <P>
 *            the parameter type
 */
public interface IExpression<R, P>
{
	/**
	 * Apply the expression for the specified parameter
	 * 
	 * @param parameter
	 *            the parameter
	 * @return the result
	 */
	R apply(P parameter);
}
