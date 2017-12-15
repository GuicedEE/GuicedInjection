/*
 * Copyright (C) 2017 Marc Magon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package za.co.mmagon.guiceinjection.logging;

import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * standard colour formatter for console output
 *
 * @author GedMarc
 * @since 14 Dec 2016
 */
public class LogSingleLineFormatter extends LogFormatter
{
	/**
	 * Ansi Colour
	 */

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * The log colour formatter
	 */
	public LogSingleLineFormatter()
	{
		//Nothing needing to be done
	}

	/**
	 * Formats according to level
	 *
	 * @param record
	 *
	 * @return
	 */
	@Override
	public String format(LogRecord record)
	{
		if (record.getMessage() == null || record.getMessage().trim().isEmpty())
		{
			return "";
		}
		if (record.getMessage().contains("visiting unvisited references"))
		{
			return "";
		}

		String output = "";

		output += "[" + sdf.format(record.getMillis()) + "]-";
		String message = "";

		if (record.getLevel().getLocalizedName().equals(Level.FINEST.getLocalizedName()))
		{
			message += record.getMessage();
		}
		else if (record.getLevel().getLocalizedName().equals(Level.FINER.getLocalizedName()))
		{
			message += record.getMessage();
		}
		else if (record.getLevel().getLocalizedName().equals(Level.FINE.getLocalizedName()))
		{
			message += record.getMessage();
		}
		else if (record.getLevel().getLocalizedName().equals(Level.CONFIG.getLocalizedName()))
		{
			message += record.getMessage();
		}
		else if (record.getLevel().getLocalizedName().equals(Level.INFO.getLocalizedName()))
		{
			message += record.getMessage();
		}
		else if (record.getLevel().getLocalizedName().equals(Level.WARNING.getLocalizedName()))
		{
			message += record.getMessage();
		}
		else if (record.getLevel().getLocalizedName().equals(Level.SEVERE.getLocalizedName()))
		{
			message += record.getMessage();
		}

		if (message.trim().isEmpty())
		{
			return "";
		}

		output = processParameters(output, record);
		output += printException(record).toString();
		output += message;

		output += " - ";
		output += "[" + record.getLevel().getLocalizedName() + "]";
		return output + System.getProperty("line.separator");
	}
}
