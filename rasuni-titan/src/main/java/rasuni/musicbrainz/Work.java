package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * A work
 *
 * @author Ralph Sigrist
 *
 */
public class Work extends Entity implements IEntity
{
	/**
	 * The work title
	 */
	@XmlElement(name = "title")
	public String _title; // NO_UCD (use final)

	/**
	 * The ISWC list
	 */
	@XmlElement(name = "iswc-list")
	public IswcList _iswcList; // NO_UCD (use final)

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
}
