package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * A release
 *
 * @author Ralph Sigrist
 *
 */
public class Release
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
	 * The title
	 */
	@XmlElement(name = "title")
	private String _title; // NO_UCD (use final)

	@XmlElement(name = "barcode")
	private String _barcode; // NO_UCD (use final)

	/**
	 * the release event list
	 */
	@XmlElement(name = "release-event-list")
	public ReleaseEventList _releaseEventList; // NO_UCD (use final)

	@XmlElement(name = "date")
	private String _date; // NO_UCD (use final)

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

	// @Override
	// public Resource getKind()
	// {
	// return Resource.RELEASE;
	// }
	/**
	 * Return the release date
	 *
	 * @return the release date
	 */
	public YearMonthDay getReleaseDate()
	{
		return YearMonthDay.min(_releaseEventList == null ? null : _releaseEventList.getReleaseDate(), _date);
	}

	/**
	 * the medium list
	 */
	@XmlElement(name = "medium-list")
	public MediumList _mediumList; // NO_UCD (use final)

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
