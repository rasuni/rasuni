package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * Release Group List
 *
 * @author Ralph Sigrist
 *
 */
public class ReleaseGroup extends Entity implements IEntity
{
	/**
	 * the title
	 */
	@XmlElement(name = "title")
	public String _title; // NO_UCD (use final)

	// empty
	@Override
	public String toString()
	{
		return _title;
	}

	@Override
	public String getName()
	{
		return _title;
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
