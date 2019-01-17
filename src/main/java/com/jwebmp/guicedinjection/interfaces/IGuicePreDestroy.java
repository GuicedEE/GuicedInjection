/*
 * Copyright (C) 2017 GedMarc
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
package com.jwebmp.guicedinjection.interfaces;

import java.util.Comparator;

/**
 * Initializes before Guice has been injected
 *
 * @author GedMarc
 * @since 15 May 2017
 */
public interface IGuicePreDestroy<J extends IGuicePreDestroy<J>>
		extends Comparator<J>, Comparable<J>
{

	/**
	 * Runs on startup
	 */
	void onDestroy();

	@Override
	default int compare(J o1, J o2)
	{
		return o1.sortOrder()
		         .compareTo(o2.sortOrder());
	}


	/**
	 * Sort order for startup, Default 100.
	 *
	 * @return the sort order never null
	 */
	default Integer sortOrder()
	{
		return 100;
	}

	@Override
	default int compareTo(J o)
	{
		if (o == null)
		{
			return -1;
		}
		int result = sortOrder().compareTo(o.sortOrder());
		if (getClass().equals(o.getClass()))
		{
			return 0;
		}
		return result == 0 ? 1 : result;
	}

}
