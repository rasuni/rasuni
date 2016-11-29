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
		AbstractList<String> arrayList = (AbstractList<String>) Unsafe.allocateInstance(Classes.forName("java.util.Arrays$ArrayList"));
		Objects.set(arrayList, "modCount", 0);
		Objects.set(arrayList, "a", new String[] { "\\\\MusikServer\\Musik", "\\\\qnap\\music", "\\\\qnap\\Qmultimedia" });
		MusicCollector.run(ConsPStacks.from(arrayList, (AbstractList<String> l) -> AbstractLists.iterator(l), i -> i.hasNext(), i -> i.next()), "ms1qnap", "\\\\MusikServer\\Musik", true);
	}
}
