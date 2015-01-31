package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The medium list
 *
 */
public class MediumList extends EntityList
{
	/**
	 * The release list
	 */
	@XmlElement(name = "medium")
	public LinkedList<Medium> _mediums = new LinkedList<>(); // NO_UCD (use
																// final)
}
