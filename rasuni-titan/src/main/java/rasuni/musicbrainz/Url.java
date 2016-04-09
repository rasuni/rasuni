package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist The URL entity
 */
public class Url extends Entity implements IEntity
{
	/**
	 * the resource
	 */
	@XmlElement(name = "resource")
	public String _resource; // NO_UCD (use final)

	@Override
	public String toString()
	{
		return _resource;
	}

	@Override
	public String getName()
	{
		return _resource;
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
