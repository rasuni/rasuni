package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

/**
 * @author Ralph Sigrist The relation list
 */
public class RelationList extends Value
{
	@XmlAttribute(name = "target-type")
	public String _targetType; // NO_UCD (use final)

	@XmlAttribute(name = "count")
	public int _count; // NO_UCD (use final)

	@XmlAttribute(name = "offset")
	public int _offset; // NO_UCD (use final)

	@XmlElement(name = "relation")
	public final LinkedList<Relation> _relations = new LinkedList<>();
}
