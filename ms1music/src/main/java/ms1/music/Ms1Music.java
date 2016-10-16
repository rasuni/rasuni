package ms1.music;

import java.util.List;
import org.pcollections.ConsPStack;
import rasuni.java.lang.Classes;
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
		List<String> arrayList = (List<String>) Unsafe.allocateInstance(Classes.forName("java.util.Arrays$ArrayList"));
		rasuni.java.lang.Objects.set(arrayList, "a", new String[] { "\\\\MusikServer\\Musik", "\\\\qnap\\music" });
		MusicCollector.run(ConsPStack.from(arrayList), "ms1music", "\\\\MusikServer\\Musik", true);
	}
}
