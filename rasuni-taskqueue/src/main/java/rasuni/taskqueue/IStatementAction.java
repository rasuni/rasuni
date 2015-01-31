package rasuni.taskqueue;




/**
 * An action for a bound statement
 * @param <T> the result type
 */
public interface IStatementAction<T>
{

	/**
	 * Execute the action
	 * @param statement the statement
	 * @return the result
	 */
	T execute(IStatement statement);
}
