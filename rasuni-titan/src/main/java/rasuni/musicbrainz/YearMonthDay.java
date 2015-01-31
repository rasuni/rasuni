package rasuni.musicbrainz;

/**
 * A year with optional month day
 *
 */
public final class YearMonthDay
{
	private final MonthDay _monthDay;
	private final int _year;

	private YearMonthDay(int year, MonthDay monthDay)
	{
		_year = year;
		_monthDay = monthDay;
	}

	/**
	 * Return the minimum value
	 *
	 * @param result
	 *            the value to compare
	 * @param date
	 *            the date
	 * @return return the minimum value
	 */
	static YearMonthDay min(YearMonthDay result, String date)
	{
		YearMonthDay parsed = parse(date);
		return isSmaller(result, parsed) ? result : parsed;
	}

	private static YearMonthDay parse(String date)
	{
		if (date == null)
		{
			return null;
		}
		else
		{
			if (date.length() == 4)
			{
				return new YearMonthDay(Integer.parseInt(date), null);
			}
			else
			{
				String text = date.substring(5);
				return new YearMonthDay(Integer.parseInt(date.substring(0, 4)), text == null ? null : text.length() == 2 ? new MonthDay(Integer.parseInt(text), null) : new MonthDay(Integer.parseInt(text.substring(0, 2)), Integer.valueOf(Integer
						.parseInt(text.substring(3)))));
			}
			// MonthDay.parse (date.substring(5)));
		}
	}

	/**
	 * Compare
	 *
	 * @param o1
	 *            first
	 * @param o2
	 *            second
	 * @return true if first is smaller
	 */
	public static boolean isSmaller(YearMonthDay o1, YearMonthDay o2)
	{
		if (o1 == null)
		{
			return false;
		}
		else
		{
			if (o2 == null)
			{
				return true;
			}
			else
			{
				if (o1._year < o2._year)
				{
					return true;
				}
				else
				{
					if (o2._year < o1._year)
					{
						return false;
					}
					else
					{
						MonthDay o11 = o1._monthDay;
						MonthDay o21 = o2._monthDay;
						if (o11 == null)
						{
							return false;
						}
						else
						{
							if (o21 == null)
							{
								return true;
							}
							else
							{
								int m1 = o11._month;
								int m2 = o21._month;
								if (m1 < m2)
								{
									return true;
								}
								else
								{
									if (m2 < m1)
									{
										return false;
									}
									else
									{
										Integer d1 = o11._day;
										if (d1 == null)
										{
											return false;
										}
										else
										{
											Integer d2 = o21._day;
											if (d2 == null)
											{
												return true;
											}
											else
											{
												return d1.intValue() < d2.intValue();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
