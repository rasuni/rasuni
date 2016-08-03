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
}
