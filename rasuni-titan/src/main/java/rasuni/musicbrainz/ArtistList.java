package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The artist list
 *
 * @author Ralph Sigrist
 *
 */
public class ArtistList extends EntityList
{
	/**
	 * The artist list
	 */
	@XmlElement(name = "artist")
	public LinkedList<Artist> _artists = new LinkedList<>(); // NO_UCD (use
																// final)
}
