package rasuni.musicbrainz;

import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;
import rasuni.lang.Value;

public class Release extends Value implements IEntity
{
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	@XmlAnyAttribute
	public HashMap<QName, String> _extensionAttributes = new HashMap<>(); // NO_UCD (use final)

	@XmlElement(name = "title")
	public String _title; // NO_UCD (use final)

	@XmlElement(name = "status")
	public String _status; // NO_UCD (use final)

	@XmlElement(name = "quality")
	public String _quality; // NO_UCD (use final)

	@XmlElement(name = "annotation")
	public Annotation _annotation; // NO_UCD (use final)

	@XmlElement(name = "disambiguation")
	public String _disambiguation; // NO_UCD (use final)

	@XmlElement(name = "packaging")
	public String _packaging; // NO_UCD (use final)

	@XmlElement(name = "text-representation")
	public TextRepresentation _textRepresentation; // NO_UCD (use final)

	@XmlElementWrapper(name = "artist-credit")
	@XmlElement(name = "name-credit")
	public final LinkedList<NameCredit> _artistCredits = new LinkedList<>(); // NO_UCD (use final)

	@XmlElement(name = "alias-list")
	public AliasList _aliasList; // NO_UCD (use final)

	@XmlElement(name = "release-group")
	public ReleaseGroup _releaseGroup; // NO_UCD (use final)

	@XmlElement(name = "date")
	public String _date;

	@XmlElement(name = "country")
	public String _country; // NO_UCD (use final)

	@XmlElement(name = "release-event-list")
	public ReleaseEventList _releaseEventList;

	@XmlElement(name = "barcode")
	public String _barcode; // NO_UCD (use final)

	@XmlElement(name = "asin")
	public String _asin; // NO_UCD (use final)

	@XmlElement(name = "cover-art-archive")
	public CoverArtArchive _coverArtArchive;

	/**
	 * The relation list
	 */
	@XmlElement(name = "relation-list")
	private final LinkedList<RelationList> _relationLists = new LinkedList<>();

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
}
