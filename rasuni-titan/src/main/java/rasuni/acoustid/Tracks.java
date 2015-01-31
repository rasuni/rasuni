package rasuni.acoustid;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * Acoustid tracks
 *
 * @author SigristR
 *
 */
public final class Tracks
{
	/**
	 * The tracks
	 */
	@XmlElement(name = "track")
	public LinkedList<AcoustId> _tracks; // NO_UCD (use final)
	// /**
	// * Add acoustids to recording
	// *
	// * @param recording the recordding
	// */
	// public void addAcoustIds(Recording recording)
	// {
	// if (_tracks != null)
	// {
	// for (Track track : _tracks)
	// {
	// recording.addAcoustId(track.getId());
	// }
	// }
	// }
}
