package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

public class Disc extends Entity
{
	@XmlElement(name = "release-list")
	public ReleaseList _releaseList; // NO_UCD (use final)
}
