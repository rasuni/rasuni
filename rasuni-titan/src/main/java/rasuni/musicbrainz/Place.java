package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * The place entity
 */
public class Place extends Entity
{
	@XmlElement(name = "name")
	private String _name; // NO_UCD (use final)

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
