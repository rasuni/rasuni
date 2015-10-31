package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

/**
 * The recording entity
 *
 */
public final class Recording extends Value implements IEntity
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
	private final LinkedList<RelationList> _relationLists = new LinkedList<>();

	/**
	 *
	 * The recording title
	 */
	@XmlElement(name = "title")
	public String _title; // NO_UCD (use final)

	/**
	 * The length
	 */
	@XmlElement(name = "length")
	private int _length; // NO_UCD (use final)

	/**
	 * the isrc list
	 */
	@XmlElement(name = "isrc-list")
	public ISRCList _isrcList; // NO_UCD (use final)

	/**
	 * the isrc list
	 */
	@XmlElement(name = "artist-credit")
	public Object _artistCredit; // NO_UCD (unused code)

	/**
	 * Return the id
	 *
	 * @return the id
	 */
	@Override
	public String getId()
	{
		return _id;
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
