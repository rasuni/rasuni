package rasuni.ms1.qnap;

import java.util.List;
import rasuni.java.lang.Classes;
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
		List<String> arrayList = (List<String>) Unsafe.allocateInstance(Classes.forName("java.util.Arrays$ArrayList"));
		rasuni.java.lang.Objects.set(arrayList, "a", new String[] { "\\\\MusikServer\\Musik", "\\\\qnap\\music", "\\\\qnap\\Qmultimedia" });
		MusicCollector.run(ConsPStacks.from(arrayList), "ms1qnap", "\\\\MusikServer\\Musik", true);
	}
}
