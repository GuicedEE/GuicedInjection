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

/**
 * Executes immediately after Guice has been initialized
 *
 * @author GedMarc
 * @since 15 May 2017
 */
public interface IGuicePostStartup<J extends IGuicePostStartup<J>>
		extends IDefaultService<J>, Runnable
{

	/**
	 * Runs immediately after the post load
	 */
	void postLoad();

	/**
	 * Sets the order in which this must run, default 100.
	 *
	 * @return the sort order to return
	 */
	@Override
	default Integer sortOrder()
	{
		return 50;
	}

	default void run()
	{
		postLoad();
	}
}
