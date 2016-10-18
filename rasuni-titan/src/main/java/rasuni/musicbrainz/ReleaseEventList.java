package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

/**
 * @author Ralph Sigrist The release event list
 *
 */
public final class ReleaseEventList extends Value
{
	@XmlAttribute(name = "count")
	public int _count; // NO_UCD (use final)

	@XmlAttribute(name = "offset")
	public int _offset; // NO_UCD (use final)

	@XmlElement(name = "release-event")
	public LinkedList<ReleaseEvent> _list = new LinkedList<>(); // NO_UCD

	// (use
	// final)
	/**
	 * Return the release date
	 *
	 * @return the release date
	 */
	YearMonthDay getReleaseDate()
	{
		YearMonthDay result = null;
		for (ReleaseEvent releaseEvent : _list)
		{
			result = YearMonthDay.min(result, releaseEvent._date);
		}
		return result;
	}
}
