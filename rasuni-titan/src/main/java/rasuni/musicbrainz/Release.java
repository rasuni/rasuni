package rasuni.musicbrainz;

import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

public class Release extends Value implements IEntity
{
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	@XmlAnyAttribute
	public HashMap<String, String> _extensionAttributes = new HashMap<>(); // NO_UCD (use final)

	@XmlElement(name = "title")
	public String _title; // NO_UCD (use final)

	@XmlElement(name = "status")
	public String _status; // NO_UCD (use final)

	@XmlElement(name = "quality")
	public String _quality; // NO_UCD (use final)

	/**
	 * The relation list
	 */
	@XmlElement(name = "relation-list")
	private final LinkedList<RelationList> _relationLists = new LinkedList<>();

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

	public LinkedList<String> getRecordings()
	{
		LinkedList<String> recordings = new LinkedList<>();
		for (Medium m : _mediumList._mediums)
		{
			recordings.addAll(m.getRecordingIds());
		}
		return recordings;
	}
}
