package rasuni.ms1.qnap;

import java.util.AbstractList;
import java.util.NoSuchElementException;
import org.pcollections.ConsPStack;
import rasuni.Stack;
import rasuni.java.lang.Classes;
import rasuni.java.lang.Objects;
import rasuni.java.util.AbstractLists;
import rasuni.java.util.Arrays;
import rasuni.org.pcollections.ConsPStacks;
import rasuni.sun.misc.Unsafe;
import rasuni.titan.MusicCollector;

/**
 * Qnap collection
 */
public class Ms1Qnap // NO_UCD (unused code)
{
	/**
	 * The entry point
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args)
	{
		@SuppressWarnings("unchecked")
		final AbstractList<String> arrayList = (AbstractList<String>) Unsafe.allocateInstance(Classes.forName("java.util.Arrays$ArrayList"));
		Objects.set(arrayList, "modCount", 0);
		Objects.set(arrayList, "a", new String[] { "/Volumes/Musik", "/Volumes/music", "/Volumes/Qmultimedia" });
		int cursor = 0;
		final int expectedModCount = 0;
		Stack<String> stack = null;
		while (cursor != Arrays.ArrayList.size(arrayList))
		{
			AbstractLists.checkForComodification(arrayList, expectedModCount);
			try
			{
				stack = new Stack<>(arrayList.get(cursor), stack);
			}
			catch (final IndexOutOfBoundsException e)
			{
				AbstractLists.checkForComodification(arrayList, expectedModCount);
				throw new NoSuchElementException();
			}
			cursor++;
		}
		@SuppressWarnings("unchecked")
		ConsPStack<String> result = (ConsPStack<String>) ConsPStacks.EMPTY;
		while (stack != null)
		{
			result = ConsPStacks.plus(result, stack._element);
			stack = stack._next;
		}
		MusicCollector.run(result, "ms1qnap", "/Volumes/Musik", true);
	}
}
