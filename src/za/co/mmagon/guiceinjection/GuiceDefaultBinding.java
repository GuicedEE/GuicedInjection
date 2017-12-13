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
package za.co.mmagon.guiceinjection;

import za.co.mmagon.guiceinjection.abstractions.GuiceInjectorModule;
import za.co.mmagon.guiceinjection.interfaces.GuiceDefaultBinder;

/**
 * Binds the global configuration as a singleton
 *
 * @author Marc Magon
 * @since 08 Jul 2017
 */
public class GuiceDefaultBinding extends GuiceDefaultBinder
{

	/*
	* Constructs a new GuiceDefaultBinding
	 */
	public GuiceDefaultBinding()
	{
		//Nothing needed
	}

	@Override
	public void onBind(GuiceInjectorModule module)
	{
		module.bind(Globals.class).asEagerSingleton();
	}
}
