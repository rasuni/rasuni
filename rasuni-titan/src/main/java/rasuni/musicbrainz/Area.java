package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Country
 *
 * @author Ralph Sigrist
 *
 */
public final class Area
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
	public final LinkedList<RelationList> _relationLists = new LinkedList<>();

	/**
	 * the name
	 */
	@XmlElement(name = "name")
	private String _name; // NO_UCD (use final)

	/**
	 * The area id
	 * 
	 * @return the area od
	 */
	public String getId()
	{
		return _id;
	}

	/**
	 * Return the name
	 *
	 * @return the name
	 */
	public String getName()
	{
		return _name;
	}

	@Override
	public String toString()
	{
		return _name;
	}
}
