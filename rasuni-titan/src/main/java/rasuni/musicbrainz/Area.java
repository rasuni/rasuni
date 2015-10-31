package rasuni.musicbrainz;

import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import rasuni.lang.Value;

@SuppressWarnings("javadoc")
public final class Area extends Value implements IEntity
{
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	@XmlAttribute(name = "type")
	public String _type; // NO_UCD (use final)

	@XmlAnyAttribute
	public HashMap<String, String> _extensionAttributes = new HashMap<>(); // NO_UCD (use final)

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
	public String _annotation; // NO_UCD (use final)

	/**
	 * The relation list
	 */
	@XmlElement(name = "relation-list")
	private final LinkedList<RelationList> _relationLists = new LinkedList<>();

	/**
	 * The area id
	 *
	 * @return the area od
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
