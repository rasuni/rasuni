package rasuni.musicbrainz;

import java.util.Objects;
import rasuni.titan.Check;

/**
 * A moth with an optional day
 *
 */
final class MonthDay
{
	final int _month;

	final Integer _day;

	MonthDay(int month, Integer day)
	{
		_month = month;
		_day = day;
	}

	@Override
	public String toString()
	{
		String month = String.format("%02d", _month);
		if (Objects.isNull(_day))
		{
			Check.fail();
			return null;
		}
		else
		{
			return month + '-' + String.format("%02d", _day);
		}
	}
}
