package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * The name credit
 *
 */
public class NameCredit
{
	/**
	 * The artist
	 */
	@XmlElement(name = "artist")
	public Artist _artist; // NO_UCD (unused code)
}
