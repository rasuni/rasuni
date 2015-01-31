package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * Release event
 *
 */
public final class ReleaseEvent
{
	/**
	 * The date
	 */
	@XmlElement(name = "date")
	String _date; // NO_UCD (use final)

	/**
	 * the area
	 */
	@XmlElement(name = "area")
	public Area _area; // NO_UCD (use final)
}
