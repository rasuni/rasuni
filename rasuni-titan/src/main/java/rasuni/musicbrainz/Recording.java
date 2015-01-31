package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * The recording entity
 *
 */
public final class Recording
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
	 *
	 * The recording title
	 */
	@XmlElement(name = "title")
	private String _title; // NO_UCD (use final)

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
	public ArtistCredit _artistCredit; // NO_UCD (unused code)

	/**
	 * Return the title
	 *
	 * @return the title
	 */
	public String getTitle()
	{
		return _title;
	}

	@Override
	public String toString()
	{
		return _title;
	}

	/**
	 * Return the id
	 * 
	 * @return the id
	 */
	public String getId()
	{
		return _id;
	}
}
