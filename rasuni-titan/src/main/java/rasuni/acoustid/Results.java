package rasuni.acoustid;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The lookup results
 *
 */
public final class Results
{
	/**
	 * The results collection
	 */
	@XmlElement(name = "result")
	public LinkedList<Result> _results; // NO_UCD (use final)
	// /**
	// * Process the results
	// * @param processor the processor
	// */
	// public void process(IEntityProcessor processor)
	// {
	// for (Result result : _results) {
	// result.process(processor);
	// }
	// }
}
