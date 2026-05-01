package com.guicedee.guicedinjection;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Lightweight UNIX cron expression parser (5 fields: minute, hour, day-of-month, month, day-of-week).
 * <p>
 * Supports:
 * <ul>
 *   <li>Single values: {@code 5}</li>
 *   <li>Ranges: {@code 1-5}</li>
 *   <li>Steps: {@code 0/15}, {@code *&#47;5}, {@code 1-30/2}</li>
 *   <li>Lists: {@code 1,3,5}</li>
 *   <li>Wildcard: {@code *}</li>
 *   <li>Named days: {@code MON-FRI}, {@code SUN}</li>
 *   <li>Named months: {@code JAN-DEC}</li>
 * </ul>
 * Day-of-week: 0=Sunday, 1=Monday ... 6=Saturday (7=Sunday alias).
 */
public final class CronExpression
{
	private static final Map<String, Integer> DAY_NAMES = Map.of(
			"SUN", 0, "MON", 1, "TUE", 2, "WED", 3,
			"THU", 4, "FRI", 5, "SAT", 6
	);

	private static final Map<String, Integer> MONTH_NAMES = new LinkedHashMap<>();
	static
	{
		MONTH_NAMES.put("JAN", 1);
		MONTH_NAMES.put("FEB", 2);
		MONTH_NAMES.put("MAR", 3);
		MONTH_NAMES.put("APR", 4);
		MONTH_NAMES.put("MAY", 5);
		MONTH_NAMES.put("JUN", 6);
		MONTH_NAMES.put("JUL", 7);
		MONTH_NAMES.put("AUG", 8);
		MONTH_NAMES.put("SEP", 9);
		MONTH_NAMES.put("OCT", 10);
		MONTH_NAMES.put("NOV", 11);
		MONTH_NAMES.put("DEC", 12);
	}

	private final String expression;
	private final Set<Integer> minutes;
	private final Set<Integer> hours;
	private final Set<Integer> daysOfMonth;
	private final Set<Integer> months;
	private final Set<Integer> daysOfWeek;
	private final boolean dayOfWeekIsWild;
	private final boolean dayOfMonthIsWild;

	private CronExpression(String expression, Set<Integer> minutes, Set<Integer> hours,
	                       Set<Integer> daysOfMonth, Set<Integer> months, Set<Integer> daysOfWeek,
	                       boolean dayOfMonthIsWild, boolean dayOfWeekIsWild)
	{
		this.expression = expression;
		this.minutes = minutes;
		this.hours = hours;
		this.daysOfMonth = daysOfMonth;
		this.months = months;
		this.daysOfWeek = daysOfWeek;
		this.dayOfMonthIsWild = dayOfMonthIsWild;
		this.dayOfWeekIsWild = dayOfWeekIsWild;
	}

	/**
	 * Parses a standard 5-field UNIX cron expression.
	 *
	 * @param expr cron expression (minute hour day-of-month month day-of-week)
	 * @return a parsed CronExpression
	 * @throws IllegalArgumentException if the expression is malformed
	 */
	public static CronExpression parse(String expr)
	{
		Objects.requireNonNull(expr, "Cron expression must not be null");
		String[] parts = expr.trim().split("\\s+");
		if (parts.length != 5)
		{
			throw new IllegalArgumentException(
					"Cron expression must have 5 fields (minute hour dom month dow): " + expr);
		}
		boolean domWild = parts[2].equals("*");
		boolean dowWild = parts[4].equals("*");

		return new CronExpression(
				expr,
				parseField(parts[0], 0, 59, null),
				parseField(parts[1], 0, 23, null),
				parseField(parts[2], 1, 31, null),
				parseField(parts[3], 1, 12, MONTH_NAMES),
				parseDayOfWeekField(parts[4]),
				domWild,
				dowWild
		);
	}

	/**
	 * Calculates the duration from the reference time until the next matching cron time.
	 *
	 * @param from the reference time
	 * @return the duration until the next execution, or empty if none within ~4 years
	 */
	public Optional<Duration> timeToNextExecution(ZonedDateTime from)
	{
		return nextExecution(from).map(next -> Duration.between(from, next));
	}

	/**
	 * Calculates the next matching time after the given reference.
	 *
	 * @param from the reference time
	 * @return the next execution time, or empty if none within ~4 years
	 */
	public Optional<ZonedDateTime> nextExecution(ZonedDateTime from)
	{
		// Start from the next whole minute
		ZonedDateTime candidate = from.plusMinutes(1).withSecond(0).withNano(0);
		ZonedDateTime limit = from.plusYears(4);

		while (candidate.isBefore(limit))
		{
			if (!months.contains(candidate.getMonthValue()))
			{
				candidate = candidate.plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
				continue;
			}
			if (!matchesDay(candidate))
			{
				candidate = candidate.plusDays(1).withHour(0).withMinute(0);
				continue;
			}
			if (!hours.contains(candidate.getHour()))
			{
				candidate = candidate.plusHours(1).withMinute(0);
				continue;
			}
			if (!minutes.contains(candidate.getMinute()))
			{
				candidate = candidate.plusMinutes(1);
				continue;
			}
			return Optional.of(candidate);
		}
		return Optional.empty();
	}

	/**
	 * Standard UNIX cron day matching: if both day-of-month and day-of-week are
	 * restricted (non-wild), the day matches if EITHER field matches. If only one
	 * is restricted, only that field is checked.
	 */
	private boolean matchesDay(ZonedDateTime dt)
	{
		boolean domMatch = daysOfMonth.contains(dt.getDayOfMonth());
		boolean dowMatch = matchesDayOfWeek(dt);

		if (dayOfMonthIsWild && dayOfWeekIsWild)
		{
			return true;
		}
		if (dayOfMonthIsWild)
		{
			return dowMatch;
		}
		if (dayOfWeekIsWild)
		{
			return domMatch;
		}
		// Both restricted: OR semantics (standard UNIX cron behavior)
		return domMatch || dowMatch;
	}

	private boolean matchesDayOfWeek(ZonedDateTime dt)
	{
		int cronDow = dt.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : dt.getDayOfWeek().getValue();
		return daysOfWeek.contains(cronDow);
	}

	private static Set<Integer> parseDayOfWeekField(String field)
	{
		Set<Integer> result = parseField(field, 0, 7, DAY_NAMES);
		// Normalize 7 (Sunday alias) to 0
		if (result.remove(7))
		{
			result.add(0);
		}
		return result;
	}

	/**
	 * Parses a single cron field into the set of matching integer values.
	 *
	 * @param field the raw field text
	 * @param min   minimum valid value
	 * @param max   maximum valid value
	 * @param names optional name-to-integer mapping (may be null)
	 * @return the expanded set of matching values
	 */
	static Set<Integer> parseField(String field, int min, int max, Map<String, Integer> names)
	{
		Set<Integer> values = new TreeSet<>();
		for (String part : field.split(","))
		{
			part = part.trim();
			if (part.contains("/"))
			{
				parseStepPart(part, min, max, names, values);
			}
			else if (part.contains("-"))
			{
				parseRangePart(part, names, values);
			}
			else if (part.equals("*"))
			{
				for (int i = min; i <= max; i++)
				{
					values.add(i);
				}
			}
			else
			{
				values.add(resolveToken(part, names));
			}
		}
		// Validate range
		for (int v : values)
		{
			if (v < min || v > max)
			{
				throw new IllegalArgumentException(
						"Value " + v + " out of range [" + min + "-" + max + "] in field: " + field);
			}
		}
		return values;
	}

	private static void parseStepPart(String part, int min, int max,
	                                   Map<String, Integer> names, Set<Integer> values)
	{
		String[] stepParts = part.split("/", 2);
		int step = Integer.parseInt(stepParts[1]);
		if (step <= 0)
		{
			throw new IllegalArgumentException("Step must be > 0 in: " + part);
		}
		int start;
		int end = max;
		if (stepParts[0].equals("*"))
		{
			start = min;
		}
		else if (stepParts[0].contains("-"))
		{
			String[] range = stepParts[0].split("-", 2);
			start = resolveToken(range[0], names);
			end = resolveToken(range[1], names);
		}
		else
		{
			start = resolveToken(stepParts[0], names);
		}
		for (int i = start; i <= end; i += step)
		{
			values.add(i);
		}
	}

	private static void parseRangePart(String part,
	                                    Map<String, Integer> names, Set<Integer> values)
	{
		String[] range = part.split("-", 2);
		int start = resolveToken(range[0], names);
		int end = resolveToken(range[1], names);
		for (int i = start; i <= end; i++)
		{
			values.add(i);
		}
	}

	/**
	 * Resolves a token to an integer, checking the names map first.
	 */
	private static int resolveToken(String token, Map<String, Integer> names)
	{
		if (names != null)
		{
			String upper = token.toUpperCase(Locale.ROOT);
			Integer mapped = names.get(upper);
			if (mapped != null)
			{
				return mapped;
			}
		}
		try
		{
			return Integer.parseInt(token);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid cron token: " + token, e);
		}
	}

	/**
	 * Returns the original cron expression string.
	 */
	@Override
	public String toString()
	{
		return expression;
	}
}


