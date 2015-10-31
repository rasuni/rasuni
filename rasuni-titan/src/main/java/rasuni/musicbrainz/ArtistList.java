package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The artist list
 *
 * @author Ralph Sigrist
 *
 */
public class ArtistList extends EntityList implements IEntityList<Artist>
{
	/**
	 * The artist list
	 */
	@XmlElement(name = "artist")
	public LinkedList<Artist> _artists = new LinkedList<>(); // NO_UCD (use

	// final)
	@Override
	public LinkedList<Artist> list()
	{
		return _artists;
	}

	@Override
	public int count()
	{
		return _count;
	}
}
