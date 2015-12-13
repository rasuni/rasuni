package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The release list
 *
 * @author Ralph Sigrist
 *
 */
public class ReleaseList extends EntityList implements IEntityList<Release>
{
	/**
	 * The release list
	 */
	@XmlElement(name = "release")
	private final LinkedList<Release> _releases = new LinkedList<>();

	@Override
	public LinkedList<Release> list()
	{
		return _releases;
	}

	@Override
	public int count()
	{
		return _count;
	}
}
