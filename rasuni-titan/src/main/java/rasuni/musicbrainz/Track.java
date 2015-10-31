package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

/**
 * A track within a release
 *
 */
public class Track extends Value
{
	/**
	 * The recording
	 */
	@XmlElement(name = "recording")
	public Recording _recording; // NO_UCD (unused code)

	/**
	 * the artist credits
	 */
	@XmlElement(name = "artist-credit")
	public Object _artistCredit; // NO_UCD (unused code)
}
