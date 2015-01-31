package rasuni.acoustid;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * Recordings
 *
 */
public final class Recordings
{
	/**
	 * The recording tag
	 */
	@XmlElement(name = "recording")
	public LinkedList<Recording> _recordings; // NO_UCD (use final)
	// /**
	// * Process the recordings
	// * @param processor the processor
	// */
	// public void process(IEntityProcessor processor)
	// {
	// for (Recording recording : _recordings) {
	// recording.process (processor);
	// }
	// }
}
