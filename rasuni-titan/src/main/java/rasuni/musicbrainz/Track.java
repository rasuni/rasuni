package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * A track within a release
 *
 */
public class Track
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
	public ArtistCredit _artistCredit; // NO_UCD (unused code)
}
