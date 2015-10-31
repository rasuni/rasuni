package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist The work list
 */
public final class WorkList extends EntityList implements IEntityList<Work>
{
	/**
	 * The artist list
	 */
	@XmlElement(name = "work")
	private final LinkedList<Work> _works = new LinkedList<>();

	@Override
	public LinkedList<Work> list()
	{
		return _works;
	}

	@Override
	public int count()
	{
		return _count;
	}
}
