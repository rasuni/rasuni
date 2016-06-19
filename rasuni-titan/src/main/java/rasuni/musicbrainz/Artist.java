package rasuni.musicbrainz;

import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import rasuni.lang.Value;

public class Artist extends Value implements IEntity
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

	@XmlElement(name = "gender")
	public String _gender; // NO_UCD (use final)

	@XmlElement(name = "country")
	public String _country; // NO_UCD (use final)

	@XmlElement(name = "area")
	public Area _area; // NO_UCD (use final)

	/**
	 * The relation list
	 */
	@XmlElement(name = "relation-list")
	private final LinkedList<RelationList> _relationLists = new LinkedList<>();

	/**
	 * Life Span
	 */
	@XmlElement(name = "ipi")
	private String _ipi; // NO_UCD (use final)

	/**
	 * the begin area
	 */
	@XmlElement(name = "begin-area")
	public Area _beginArea; // NO_UCD (use final)

	/**
	 * The end area
	 */
	@XmlElement(name = "end-area")
	public Area _endArea; // NO_UCD (use final)

	/**
	 * The name
	 *
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return _name;
	}

	@Override
	public String toString()
	{
		return _name;
	}

	@Override
	public String getId()
	{
		return _id;
	}

	@Override
	public LinkedList<RelationList> getRelationLists()
	{
		return _relationLists;
	}
}
