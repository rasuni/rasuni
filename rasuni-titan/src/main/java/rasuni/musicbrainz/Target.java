package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Target
 *
 * @author Ralph Sigrist
 *
 */
public class Target
{
	/**
	 * id
	 */
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)
	/**
	 * content
	 */
	@XmlValue
	public String _target; // NO_UCD (use final)
}
