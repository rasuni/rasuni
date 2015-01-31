package rasuni.musicbrainz;

import com.thinkaurelius.titan.core.TitanGraph;
import javax.xml.bind.annotation.XmlEnumValue;
import rasuni.titan.TitanCollector;

/**
 * Entity kind
 *
 * @author Ralph Sigrist
 *
 */
public enum Resource
{
	/**
	 * A recording
	 */
	@XmlEnumValue("recording")
	RECORDING("recording", "recording.mbid"),
	/**
	 * Artist
	 */
	@XmlEnumValue("artist")
	ARTIST("artist", "artist.mbid"),
	/**
	 * Release
	 */
	@XmlEnumValue("release")
	RELEASE("release", "release.mbid"),
	/**
	 * Work
	 */
	@XmlEnumValue("work")
	WORK("work", "work.mbid"),
	/**
	 * Release Group
	 */
	@XmlEnumValue("release_group")
	RELEASE_GROUP("release-group", "release-group.mbid"),
	/**
	 * URL
	 */
	@XmlEnumValue("url")
	URL("url", "url.mbid"),
	/**
	 * label
	 */
	@XmlEnumValue("label")
	LABEL("label", "label.mbid"),
	/**
	 * areas
	 */
	@XmlEnumValue("area")
	AREA("area", "area.mbid"),
	/**
	 * place
	 */
	@XmlEnumValue("place")
	PLACE("place", "place.mbid"),
	/**
	 * the collection
	 */
	@XmlEnumValue("collection")
	COLLECTION("collection", "collection.mbid"),
	/**
	 * the isrc resource
	 */
	ISRC("isrc", "isrc.mbid"),
	/**
	 * the disc resource
	 */
	DISC_ID("discid", "discid.mbid"),
	/**
	 * series
	 */
	@XmlEnumValue("series")
	SERIES("series", "series.mbid"),
	/**
	 * series
	 */
	@XmlEnumValue("event")
	EVENT("event", "event.mbid");
	private String _name;

	private String _mbid;

	private Resource(String name, String mbid)
	{
		_name = name;
		_mbid = mbid;
	}

	/**
	 * Return the resource name
	 * @return the resource name
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * return the id key name
	 * @return the id key name
	 */
	public String getMBID()
	{
		return _mbid;
	}

	/**
	 * Register a key to the provided titan graph
	 *
	 * @param tg
	 *            the graph
	 */
	public void makeKey(TitanGraph tg)
	{
		TitanCollector.primaryKey(TitanCollector.string(getMBID()), tg);
	}
}
