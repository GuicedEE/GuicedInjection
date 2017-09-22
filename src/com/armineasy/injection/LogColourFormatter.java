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
public class LogColourFormatter extends java.util.logging.Formatter
{
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_RESET = "\u001b[0m";
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_BLACK = "\u001b[30m";
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_RED = "\u001b[31m";
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_GREEN = "\u001b[32m";
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_YELLOW = "\u001b[33m";
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_BLUE = "\u001b[34m";
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_PURPLE = "\u001b[35m";
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_CYAN = "\u001b[36m";
	/**
	 * Ansi Colour
	 */
	public static final String ANSI_WHITE = "\u001b[37m";
	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
	
	public static boolean INVERTED = false;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss.SSS");
	
	/**
	 * The log colour formatter
	 */
	public LogColourFormatter()
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
		
		
		String output = "";
		if (!INVERTED)
		{
			output += ANSI_WHITE;
			output += ANSI_BLACK_BACKGROUND;
		}
		output += "[" + sdf.format(record.getMillis()) + "]-";
		
		if (record.getLevel() == Level.FINEST)
		{
			output += ANSI_RED
					+ ANSI_BLACK_BACKGROUND
					+ record.getMessage().toString();
		}
		else if (record.getLevel() == Level.FINER)
		{
			output += ANSI_CYAN
					+ ANSI_BLACK_BACKGROUND + record.getMessage().toString();
		}
		else if (record.getLevel() == Level.FINE)
		{
			output += ANSI_BLUE
					+ ANSI_BLACK_BACKGROUND + record.getMessage().toString();
		}
		else if (record.getLevel() == Level.CONFIG)
		{
			output += ANSI_PURPLE
					+ ANSI_BLACK_BACKGROUND + record.getMessage().toString();
		}
		else if (record.getLevel() == Level.INFO)
		{
			output += ANSI_GREEN
					+ ANSI_BLACK_BACKGROUND + record.getMessage().toString();
		}
		else if (record.getLevel() == Level.WARNING)
		{
			output += ANSI_YELLOW +
					ANSI_BLACK_BACKGROUND + record.getMessage().toString();
		}
		else if (record.getLevel() == Level.SEVERE)
		{
			output += ANSI_RED +
					ANSI_BLACK_BACKGROUND + record.getMessage().toString();
		}
		
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
			output += ANSI_RESET;
			output += sw.toString();
		}
		
		if (!INVERTED)
		{
			if (!(record.getThrown() != null))
			{
				output += ANSI_RESET;
				output += ANSI_BLACK_BACKGROUND;
				output += ANSI_WHITE;
				
			}
			else
			{
				output += ANSI_BLACK;
			}
		}
		
		output += " - ";
		
		output += "[" + record.getLevel().getLocalizedName() + "]";
		
		return output + System.getProperty("line.separator");
	}
}
