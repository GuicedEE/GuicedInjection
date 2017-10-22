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
package com.armineasy.injection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * standard colour formatter for console output
 *
 * @author GedMarc
 * @since 14 Dec 2016
 */
public class LogSingleLineFormatter extends java.util.logging.Formatter
{
	/**
	 * Ansi Colour
	 */

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss.SSS");

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

		output += message;
		output = configureParameters(record, output);

		output += " - ";
		output += "[" + record.getLevel().getLocalizedName() + "]";
		return output + System.getProperty("line.separator");
	}

	private String configureParameters(LogRecord record, String output)
	{
		if (record.getParameters() != null && record.getParameters().length > 0)
		{
			for (int n = 0; n < record.getParameters().length; n++)
			{
				Object o = record.getParameters()[n];
				if (o == null)
				{
					continue;
				}
				String replace = "\\{" + n + "\\}";
				String replacable = o.toString();
				output = output.replaceAll(replace, replacable);
			}
		}

		if (record.getThrown() != null)
		{
			Throwable t = record.getThrown();
			StringWriter sw = new StringWriter();
			try (PrintWriter pw = new PrintWriter(sw))
			{
				t.printStackTrace(pw);
			}
			output += sw.toString();
		}

		return output;
	}
}
