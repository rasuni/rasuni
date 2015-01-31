package rasuni.acoustid;

import javax.xml.bind.annotation.XmlElement;

/**
 * acoustid recording
 *
 */
final class Recording
{
	@XmlElement(name = "sources")
	private int _sources; // NO_UCD (use final)

	@XmlElement(name = "id")
	private String _id; // NO_UCD (use final)
}
