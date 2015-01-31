package rasuni.acoustid;

import javax.xml.bind.annotation.XmlElement;

/**
 * the lookup result
 *
 */
public final class Result
{
	/**
	 * the recordings tag
	 */
	@XmlElement(name = "recordings")
	public Recordings _recordings; // NO_UCD (use final)
	// /**
	// * Process the result
	// * @param processor the processor
	// */
	// public void process(IEntityProcessor processor)
	// {
	// _recordings.process (processor);
	// }
}
