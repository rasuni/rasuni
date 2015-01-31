package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * Release Group List
 *
 * @author Ralph Sigrist
 *
 */
public class ReleaseGroupList extends EntityList
{
	/**
	 * The artist list
	 */
	@XmlElement(name = "release-group")
	public LinkedList<ReleaseGroup> _releaseGroups = new LinkedList<>(); // NO_UCD
																			// (use
																			// final)
}
