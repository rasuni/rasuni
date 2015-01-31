package rasuni.taskqueue;

/**
 * @author Ralph Sigrist
 * a task handler
 */
public interface ITaskHandler
{

	/**
	 * Handle a task
	 * @param tq the context
	 * @param taskId the task id
	 * @param taskKind the the task kind
	 * @param foreignId the foreign entity id
	 * @param long1 the task id
	 * @return true if continue handle
	 */
	HandlerResult handle(ITaskContext tq, long taskId, int taskKind, String foreignId);
}
