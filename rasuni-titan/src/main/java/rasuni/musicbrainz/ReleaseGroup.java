package rasuni.musicbrainz;

import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import rasuni.lang.Value;

/**
 * Release Group List
 *
 * @author Ralph Sigrist
 *
 */
public class ReleaseGroup extends Value implements IEntity
{
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	@XmlAttribute(name = "type")
	public String _type; // NO_UCD (use final)

	@XmlAnyAttribute
	public HashMap<QName, String> _extensionAttributes = new HashMap<>(); // NO_UCD (use final)

	@XmlElement(name = "title")
	public String _title; // NO_UCD (use final)

	@XmlElement(name = "annotation")
	public Annotation _annotation; // NO_UCD (use final)

	@XmlElement(name = "disambiguation")
	public String _disambiguation; // NO_UCD (use final)

	@XmlElement(name = "first-release-date")
	public String _firstReleaseDate; // NO_UCD (use final)

	@XmlElement(name = "primary-type")
	public String _primaryType; // NO_UCD (use final)

	@XmlElementWrapper(name = "secondary-type-list")
	@XmlElement(name = "secondary-type")
	public LinkedList<String> _secondaryTypes = new LinkedList<>(); // NO_UCD (use final)

	@XmlElementWrapper(name = "artist-credit")
	@XmlElement(name = "name-credit")
	public final LinkedList<NameCredit> _artistCredits = new LinkedList<>(); // NO_UCD (use final)

	@XmlElement(name = "release-list")
	public ReleaseList _releaseList; // NO_UCD (use final)

	@XmlElement(name = "alias-list")
	public AliasList _aliasList; // NO_UCD (use final)

	@XmlElement(name = "relation-list")
	public LinkedList<RelationList> _relationLists = new LinkedList<>(); // NO_UCD (use final)

	@XmlElement(name = "tag-list")
	public TagList _tagList; // NO_UCD (use final)

	@XmlElement(name = "user-tag-list")
	public UserTagList _userTagList; // NO_UCD (use final)

	@XmlElement(name = "rating")
	public Rating _rating; // NO_UCD (use final)

	@XmlElement(name = "user-rating")
	public Integer _userRating;

	@XmlAnyElement
	public final LinkedList<Element> _elementExtensions = new LinkedList<>();

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
