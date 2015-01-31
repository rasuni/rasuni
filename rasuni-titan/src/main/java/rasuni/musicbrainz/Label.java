package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist a label
 *
 */
public class Label extends Entity
{
	/**
	 * The name
	 */
	@XmlElement(name = "name")
	private String _name; // NO_UCD (use final)

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
