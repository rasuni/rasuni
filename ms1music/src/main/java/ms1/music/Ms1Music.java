package ms1.music;

import rasuni.java.util.AbstractLists;
import java.util.AbstractList;
import rasuni.java.lang.Classes;
import rasuni.java.lang.Objects;
import rasuni.org.pcollections.ConsPStacks;
import rasuni.sun.misc.Unsafe;
import rasuni.titan.MusicCollector;

/**
 * Qnap collection
 */
public class Ms1Music // NO_UCD (unused code)
{
	/**
	 * The entry point
	 *
	 * @param args
	 *            the arguments
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		AbstractList<String> arrayList = (AbstractList<String>) Unsafe.allocateInstance(Classes.forName("java.util.Arrays$ArrayList"));
		AbstractLists.construct(arrayList);
		Objects.set(arrayList, "a", new String[] { "\\\\MusikServer\\Musik", "\\\\qnap\\music" });
		MusicCollector.run(ConsPStacks.from(arrayList, l -> l.iterator(), i -> i.hasNext(), i -> i.next()), "ms1music", "\\\\MusikServer\\Musik", true);
	}
}
