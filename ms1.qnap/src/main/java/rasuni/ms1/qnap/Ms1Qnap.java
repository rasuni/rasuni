package rasuni.ms1.qnap;

import java.util.AbstractList;
import rasuni.java.lang.Classes;
import rasuni.java.lang.Objects;
import rasuni.java.util.AbstractLists;
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
		MusicCollector.run(ConsPStacks.from(arrayList, AbstractLists::iterator, AbstractLists.Itr::hasNext, i -> AbstractLists.Itr.next(i)), "ms1qnap.mac", "/Volumes/Musik", true);
	}
}
