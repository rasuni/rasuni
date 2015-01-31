package rasuni.musicbrainz;

import rasuni.functional.IExpression;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * A recording list
 *
 * @author Ralph Sigrist
 *
 */
public class RecordingList extends EntityList
{
	/**
	 * A recording list
	 */
	@XmlElement(name = "recording")
	public final LinkedList<Recording> _recordings = new LinkedList<>();

	/**
	 * the accessor
	 */
	public static final IExpression<LinkedList<Recording>, RecordingList> LIST = recordingList -> recordingList._recordings;
}
