package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist The release event list
 *
 */
public final class ReleaseEventList
{
	/**
	 * the release events
	 */
	@XmlElement(name = "release-event")
	public LinkedList<ReleaseEvent> _releaseEvents = new LinkedList<>(); // NO_UCD
																			// (use
																			// final)

	/**
	 * Return the release date
	 *
	 * @return the release date
	 */
	public YearMonthDay getReleaseDate()
	{
		YearMonthDay result = null;
		for (ReleaseEvent releaseEvent : _releaseEvents)
		{
			result = YearMonthDay.min(result, releaseEvent._date);
		}
		return result;
	}
}
