package rasuni.musicbrainz;

import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;
import rasuni.lang.Value;

public final class Area extends Value implements IEntity
{
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	@XmlAttribute(name = "type")
	public String _type; // NO_UCD (use final)

	@XmlAnyAttribute
	public HashMap<QName, String> _extensionAttributes = new HashMap<>(); // NO_UCD (use final)

	@XmlElement(name = "name")
	public String _name; // NO_UCD (use final)

	@XmlElement(name = "sort-name")
	public String _sortName; // NO_UCD (use final)

	@XmlElement(name = "disambiguation")
	public String _disambiguation; // NO_UCD (use final)

	@XmlElementWrapper(name = "iso-3166-1-code-list")
	@XmlElement(name = "iso-3166-1-code")
	public LinkedList<String> _iso3166_1_CodeList = new LinkedList<>(); // NO_UCD (use final)

	@XmlElementWrapper(name = "iso-3166-1-code-list")
	@XmlElement(name = "iso-3166-2-code")
	public LinkedList<String> _iso3166_2_CodeList = new LinkedList<>(); // NO_UCD (use final)

	@XmlElementWrapper(name = "iso-3166-3-code-list")
	@XmlElement(name = "iso-3166-3-code")
	public LinkedList<String> _iso3166_3_CodeList = new LinkedList<>(); // NO_UCD (use final)

	@XmlElement(name = "annotation")
	public Annotation _annotation; // NO_UCD (use final)

	@XmlElement(name = "life-span")
	public LifeSpan _lifeSpan; // NO_UCD (use final)

	@XmlElement(name = "alias-list")
	public AliasList _aliasList; // NO_UCD (use final)

	@XmlElement(name = "relation-list")
	public final LinkedList<RelationList> _relationLists = new LinkedList<>();

	/**
	 * The area id
	 *
	 * @return the area id
	 */
	@Override
	public String getId()
	{
		return _id;
	}

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
	public LinkedList<RelationList> getRelationLists()
	{
		return _relationLists;
	}
}
