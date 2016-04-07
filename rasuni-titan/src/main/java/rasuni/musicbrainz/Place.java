package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The place entity
 */
public class Place extends Entity implements IEntity
{
	@XmlElement(name = "name")
	private String _name; // NO_UCD (use final)

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

	@Override
	public String toString()
	{
		return _name;
	}

	@Override
	public LinkedList<RelationList> getRelationLists()
	{
		return _relationLists;
	}

	@Override
	public String getId()
	{
		return _id;
	}
}
