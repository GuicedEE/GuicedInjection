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
package com.guicedee.guicedinjection.interfaces;

import com.google.inject.AbstractModule;

/**
 * @param <M> The module to bind
 * @author GedMarc
 */
@FunctionalInterface
public interface IDefaultBinder<M extends AbstractModule> {

	/**
	 * Performs the binding with the injection module that is required
	 *
	 * @param module The module being passed in
	 */
	void onBind(M module);


}
