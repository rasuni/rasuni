package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlEnumValue;

public enum RelationDirection
{
	/**
	 *
	 */
	@XmlEnumValue("both")
	BOTH, // NO_UCD (unused code)
	/**
	 *
	 */
	@XmlEnumValue("forward")
	FORWARD,
	/**
	 *
	 */
	@XmlEnumValue("backward")
	BACKWARD,
}
