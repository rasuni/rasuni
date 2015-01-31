package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The isrc list
 *
 */
public class ISRCList extends EntityList
{
	/**
	 * the list
	 */
	@XmlElement(name = "isrc")
	public LinkedList<ISRC> _isrcs = new LinkedList<>(); // NO_UCD (use final)
}
