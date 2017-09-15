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
package com.armineasy.injection.interfaces;

import com.armineasy.injection.abstractions.GuiceSiteInjectorModule;

import java.util.Comparator;

/**
 * Assists to bind into the injection module from multiple JAR or enterprise archives
 *
 * @author GedMarc
 * @since 12 Dec 2016
 */
public abstract class GuiceSiteBinder implements Comparator<GuiceSiteBinder>, DefaultBinder<GuiceSiteInjectorModule>
{
	
	/**
	 * A default regex to identify query parameters
	 */
	protected static String QueryParametersRegex = "(\\?.*)?";
	/**
	 * Default order 100
	 */
	private int DefaultSortOrder = 100;
	
	/**
	 * Blank constructor
	 */
	public GuiceSiteBinder()
	{
		//Nothing needed to be done
	}
	
	/**
	 * The default sort order number is 100
	 *
	 * @return
	 */
	public Integer sortOrder()
	{
		return DefaultSortOrder;
	}
	
	/**
	 * Sets the default sort order
	 *
	 * @param DefaultSortOrder
	 */
	public void setDefaultSortOrder(int DefaultSortOrder)
	{
		this.DefaultSortOrder = DefaultSortOrder;
	}
	
	/**
	 * Compares the items across
	 *
	 * @param o1
	 * @param o2
	 *
	 * @return
	 */
	@Override
	public int compare(GuiceSiteBinder o1, GuiceSiteBinder o2)
	{
		if (o1 == null || o2 == null)
		{
			return -1;
		}
		return o1.sortOrder().compareTo(o2.sortOrder());
	}
}
