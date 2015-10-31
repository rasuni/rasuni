package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlEnumValue;

public enum RelationDirection
{
	FORWARD,
	/**
	 * backward
	 */
	@XmlEnumValue("backward")
	BACKWARD,
}
