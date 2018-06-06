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
package com.jwebmp.guiceinjection.annotations;

import com.google.inject.Module;

/**
 * Executes immediately after Guice has been initialized
 *
 * @author Marc Magon
 * @since 15 May 2017
 */
@GuiceInjectorModuleMarker
public interface GuiceStartupModule
{

	/**
	 * Runs immediately after the post load
	 */
	void load(Module module);

	/**
	 * Sets the order in which this must run, default 100.
	 *
	 * @return
	 */
	Integer sortOrder();
}
