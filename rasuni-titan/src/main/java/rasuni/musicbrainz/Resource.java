package rasuni.musicbrainz;

import java.util.Objects;
import javax.xml.bind.annotation.XmlEnumValue;

public enum Resource
{
	/**
	 * A recording
	 */
	@XmlEnumValue("recording")
	RECORDING("recording"),
	/**
	 * Artist
	 */
	@XmlEnumValue("artist")
	ARTIST("artist"),
	/**
	 * Release
	 */
	@XmlEnumValue("release")
	RELEASE("release"),
	/**
	 * Work
	 */
	@XmlEnumValue("work")
	WORK("work"),
	/**
	 * Release Group
	 */
	@XmlEnumValue("release_group")
	RELEASE_GROUP("release-group"),
	/**
	 * URL
	 */
	@XmlEnumValue("url")
	URL("url"),
	/**
	 * label
	 */
	@XmlEnumValue("label")
	LABEL("label"),
	/**
	 * areas
	 */
	@XmlEnumValue("area")
	AREA("area"),
	/**
	 * place
	 */
	@XmlEnumValue("place")
	PLACE("place"),
	/**
	 * the collection
	 */
	@XmlEnumValue("collection")
	COLLECTION(null),
	/**
	 * the isrc resource
	 */
	ISRC("isrc"),
	/**
	 * the disc resource
	 */
	DISC_ID("discid"),
	/**
	 * series
	 */
	@XmlEnumValue("series")
	SERIES("series"),
	/**
	 * series
	 */
	@XmlEnumValue("event")
	EVENT("event");
	private final String _name;

	private String _mbid = null;

	private Resource(String name)
	{
		_name = name;
	}

	public static String mbid(String resourceName)
	{
		return resourceName + ".mbid";
	}

	public String getName()
	{
		return _name;
	}

	public String getMBID()
	{
		if (Objects.isNull(_mbid))
		{
			_mbid = mbid(_name);
		}
		return _mbid;
	}
}
