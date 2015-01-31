package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The artist credit
 *
 */
public class ArtistCredit
{
	/**
	 * The name credits
	 */
	@XmlElement(name = "name-credit")
	public final LinkedList<NameCredit> _nameCredits = new LinkedList<>();
}
