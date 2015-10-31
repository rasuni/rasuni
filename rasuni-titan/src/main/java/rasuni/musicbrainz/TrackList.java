package rasuni.musicbrainz;

import java.util.Collection;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

@SuppressWarnings("javadoc")
public class TrackList extends Value
{
	/**
	 * the tracks
	 */
	@XmlElement(name = "track")
	private final LinkedList<Track> _tracks = new LinkedList<>();

	Collection<String> getRecordingIds()
	{
		LinkedList<String> recordings = new LinkedList<>();
		for (Track track : _tracks)
		{
			recordings.add(track._recording._id);
		}
		return recordings;
	}
}
