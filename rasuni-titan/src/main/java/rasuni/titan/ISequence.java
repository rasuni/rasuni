package rasuni.titan;

/**
 *
 * A lazy (not yet evaluated), immutable, singly linked list.
 *
 * @param <T> the sequence member type
 */
public interface ISequence<T>
{
	/**
	 * get the first sequence member
	 * @return the first sequence member
	 */
	T getHead();

	/**
	 * get the remaining sequence, without the first member. <code>null</code> is return, when there are no more members.
	 * @return the remaining sequence
	 */
	ISequence<T> getTail();
}
