package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist The relation list
 */
public class RelationList
{
	/**
	 * Target Type
	 */
	@XmlAttribute(name = "target-type")
	public Resource _targetType; // NO_UCD (use final)

	/**
	 * The relation
	 */
	@XmlElement(name = "relation")
	public final LinkedList<Relation> _relations = new LinkedList<>();
}
