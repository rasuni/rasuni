package rasuni.acoustid;

import javax.xml.bind.annotation.XmlElement;

/**
 * AcoustID track
 *
 */
public final class AcoustId
{
	/**
	 * The track id
	 */
	@XmlElement(name = "id")
	public String _id; // NO_UCD (use final)
}
