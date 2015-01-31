package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The disc list
 *
 */
public class DiscList extends EntityList
{
	/**
	 * The disc list
	 */
	@XmlElement(name = "disc")
	public LinkedList<Disc> _discs = new LinkedList<>(); // NO_UCD (use final)
}
