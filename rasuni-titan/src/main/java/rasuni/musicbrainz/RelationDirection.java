package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlEnumValue;

@SuppressWarnings("javadoc")
public enum RelationDirection
{
	FORWARD,
	/**
	 * backward
	 */
	@XmlEnumValue("backward")
	BACKWARD,
}
