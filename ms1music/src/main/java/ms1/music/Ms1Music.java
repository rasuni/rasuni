package ms1.music;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.function.Function;
import rasuni.java.lang.Classes;
import rasuni.java.lang.Objects;
import rasuni.java.util.AbstractLists;
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
		final AbstractList<String> arrayList = (AbstractList<String>) Unsafe.allocateInstance(Classes.forName("java.util.Arrays$ArrayList"));
		Objects.set(arrayList, "modCount", 0);
		Objects.set(arrayList, "a", new String[] { "/Volumes/Musik", "/Volumes/music" });
		MusicCollector.run(ConsPStacks.from(arrayList, (Function<AbstractList<String>, Iterator<String>>) AbstractLists::iterator, AbstractLists.Itr::hasNext, i -> i.next()), "ms1music.mac", "/Volumes/Musik", true);
	}
}
