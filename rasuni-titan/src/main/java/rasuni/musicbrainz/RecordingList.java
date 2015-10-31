package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * A recording list
 *
 * @author Ralph Sigrist
 *
 */
public class RecordingList extends EntityList implements IEntityList<Recording>
{
	/**
	 * A recording list
	 */
	@XmlElement(name = "recording")
	public final LinkedList<Recording> _recordings = new LinkedList<>();

	@Override
	public LinkedList<Recording> list()
	{
		return _recordings;
	}

	@Override
	public int count()
	{
		return _count;
	}
}
