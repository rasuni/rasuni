package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist Base class for entities
 */
public abstract class Entity
{
	/**
	 * The id
	 */
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	/**
	 * The relation list
	 */
	@XmlElement(name = "relation-list")
	final LinkedList<RelationList> _relationLists = new LinkedList<>();

	/**
	 * Return the id
	 *
	 * @return the id
	 */
	public final String getId()
	{
		return _id;
	}

	@Override
	public String toString()
	{
		return _id;
	}
}
