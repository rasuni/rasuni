package rasuni.musicbrainz;

import rasuni.filesystemscanner.impl.Check;
import java.util.Objects;

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
