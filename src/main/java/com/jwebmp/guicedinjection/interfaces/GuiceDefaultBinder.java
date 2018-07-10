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
package com.jwebmp.guicedinjection.interfaces;

import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Maps URL's to Servlet classes across multiple JAR's and/or enterprise archives
 *
 * @author GedMarc
 * @since 12 Dec 2016
 */
public abstract class GuiceDefaultBinder
		implements Comparator<GuiceDefaultBinder>, DefaultBinder<GuiceInjectorModule>, Serializable, Comparable<GuiceDefaultBinder>
{

	private static final long serialVersionUID = 1L;
	private int sortOrder = 100;

	/**
	 * Blank constructor
	 */
	public GuiceDefaultBinder()
	{
		//Nothing needed to do on constructions
	}

	/**
	 * Sets this sort order
	 *
	 * @param DefaultSortOrder the sort order to use
	 */
	@SuppressWarnings("unused")
	public void setSortOrder(int DefaultSortOrder)
	{
		sortOrder = DefaultSortOrder;
	}

	@Override
	public int compare(GuiceDefaultBinder o1, GuiceDefaultBinder o2)
	{
		if (o1 == null || o2 == null)
		{
			return -1;
		}
		return o1.sortOrder()
		         .compareTo(o2.sortOrder());
	}

	/**
	 * The default value is 100
	 *
	 * @return the sort order applied default 100
	 */
	@SuppressWarnings("WeakerAccess")
	public Integer sortOrder()
	{
		return sortOrder;
	}

	@Override
	public int compareTo(@NotNull GuiceDefaultBinder o)
	{
		int output = sortOrder().compareTo(o.sortOrder());
		if (output == 0)
		{
			return -1;
		}
		return output;
	}

}
