package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * isrc
 *
 */
public class ISRC
{
	/**
	 * the id
	 */
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)
	/**
	 * the recording list
	 */
	@XmlElement(name = "recording-list")
	public RecordingList _recordingList; // NO_UCD (use final)
}
