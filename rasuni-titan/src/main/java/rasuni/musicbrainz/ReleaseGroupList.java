package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * Release Group List
 *
 * @author Ralph Sigrist
 *
 */
public class ReleaseGroupList extends EntityList implements IEntityList<ReleaseGroup>
{
	/**
	 * The artist list
	 */
	@XmlElement(name = "release-group")
	public LinkedList<ReleaseGroup> _releaseGroups = new LinkedList<>(); // NO_UCD

	// (use
	// final)
	@Override
	public LinkedList<ReleaseGroup> list()
	{
		return _releaseGroups;
	}

	@Override
	public int count()
	{
		return _count;
	}
}
