package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * The artist
 *
 * @author Ralph Sigrist
 */
public class Artist // extends Entity
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
	 * Return the id
	 *
	 * @return the id
	 */
	public String getId()
	{
		return _id;
	}

	/**
	 * The name
	 */
	@XmlElement(name = "name")
	private String _name; // NO_UCD (use final)

	/**
	 * The sort name
	 */
	@XmlElement(name = "sort-name")
	private String _sortName; // NO_UCD (use final)

	/**
	 * Country
	 */
	@XmlElement(name = "area")
	public Area _area; // NO_UCD (use final)

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
	public String getName()
	{
		return _name;
	}

	@Override
	public String toString()
	{
		return _name;
	}
}
