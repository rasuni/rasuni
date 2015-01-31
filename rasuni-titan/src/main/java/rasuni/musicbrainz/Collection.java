package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * A collection entity
 */
public final class Collection extends Entity
{
	/**
	 * The collection name
	 */
	@XmlElement(name = "name")
	public String _name; // NO_UCD (use final)
	/**
	 * The release list
	 */
	@XmlElement(name = "release-list")
	public ReleaseList _releaseList; // NO_UCD (use final)
}
