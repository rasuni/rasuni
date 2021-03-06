package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

public class Medium extends Value
{
	/**
	 * The disc list
	 */
	@XmlElement(name = "disc-list")
	public DiscList _discs; // NO_UCD (use final)

	/**
	 * the track list
	 */
	@XmlElement(name = "track-list")
	public TrackList _trackList; // NO_UCD (use final)
}
