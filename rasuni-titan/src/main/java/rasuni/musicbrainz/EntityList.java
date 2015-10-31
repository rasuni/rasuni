package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlAttribute;
import rasuni.lang.Value;

/**
 * @author Ralph Sigrist Base class for entity lists
 */
public abstract class EntityList extends Value
{
	/**
	 * The count
	 */
	@XmlAttribute(name = "count")
	public int _count; // NO_UCD (use final)

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
