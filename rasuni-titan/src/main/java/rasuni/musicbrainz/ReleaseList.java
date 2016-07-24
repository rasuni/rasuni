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
public class ReleaseList extends Value
{
	@XmlAttribute(name = "count")
	public int _count; // NO_UCD (use final)

	@XmlAttribute(name = "offset")
	public int _offset; // NO_UCD (use final)

	/**
	 * The release list
	 */
	@XmlElement(name = "release")
	private final LinkedList<Release> _releases = new LinkedList<>();

	public LinkedList<Release> list()
	{
		return _releases;
	}

	public int count()
	{
		return _count;
	}

	/**
	 * Compare the count
	 *
	 * @param compare
	 *            the compare value
	 * @return true if equal
	 */
	public final boolean isCount(int compare)
	{
		return _count == compare;
	}
}
