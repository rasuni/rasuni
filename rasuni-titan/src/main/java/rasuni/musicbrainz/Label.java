package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist a label
 *
 */
public class Label extends Entity implements IEntity
{
	/**
	 * The name
	 */
	@XmlElement(name = "name")
	public String _name; // NO_UCD (use final)

	/**
	 * the area
	 */
	@XmlElement(name = "area")
	public Area _area; // NO_UCD (use final)

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
	public String toString()
	{
		return _name;
	}

	@Override
	public LinkedList<RelationList> getRelationLists()
	{
		return _relationLists;
	}
}
