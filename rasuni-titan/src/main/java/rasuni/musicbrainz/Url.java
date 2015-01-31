package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist The URL entity
 */
public class Url extends Entity
{
	/**
	 * the resource
	 */
	@XmlElement(name = "resource")
	public String _resource; // NO_UCD (use final)

	@Override
	public String toString()
	{
		return _resource;
	}
}
