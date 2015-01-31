package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The track list
 *
 */
public class TrackList
{
	/**
	 * the tracks
	 */
	@XmlElement(name = "track")
	public final LinkedList<Track> _tracks = new LinkedList<>();
}
