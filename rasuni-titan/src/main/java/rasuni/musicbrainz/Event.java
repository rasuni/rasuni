package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * The event entity
 *
 */
public class Event implements IEntity
{
	/**
	 * The id
	 */
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	/**
	 * the name
	 */
	@XmlElement(name = "name")
	private String _name; // NO_UCD (use final)

	/**
	 * The event id
	 *
	 * @return the event id
	 */
	@Override
	public String getId()
	{
		return _id;
	}

	/**
	 * Return the name
	 *
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return _name;
	}

	/**
	 * The relation list
	 */
	@XmlElement(name = "relation-list")
	private final LinkedList<RelationList> _relationLists = new LinkedList<>();

	@Override
	public LinkedList<RelationList> getRelationLists()
	{
		return _relationLists;
	}
}
