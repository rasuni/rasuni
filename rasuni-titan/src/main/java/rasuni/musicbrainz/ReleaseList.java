package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

/**
 * The release list
 *
 * @author Ralph Sigrist
 *
 */
public class ReleaseList extends Value //implements IEntityList<Release>
{
	@XmlAttribute(name = "count")
	public int _count; // NO_UCD (use final)

	@XmlAttribute(name = "offset")
	public int _offset; // NO_UCD (use final)

	@XmlElement(name = "release")
	public final LinkedList<Release> _releases = new LinkedList<>();
}
