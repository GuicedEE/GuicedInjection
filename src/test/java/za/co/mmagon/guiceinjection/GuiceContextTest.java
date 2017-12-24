/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.mmagon.guiceinjection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import za.co.mmagon.guiceinjection.abstractions.GuiceInjectorModule;
import za.co.mmagon.guiceinjection.annotations.GuiceInjectorModuleMarker;
import za.co.mmagon.guiceinjection.interfaces.GuiceDefaultBinder;
import za.co.mmagon.guiceinjection.interfaces.GuiceSiteBinder;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author GedMarc
 */
public class GuiceContextTest extends GuiceDefaultBinder
{

	@BeforeAll
	public static void pre()
	{
		LogManager.getLogManager().getLogger("").setLevel(Level.FINEST);
	}

	@Test
	public void main()
	{
		for (Handler handler : Logger.getLogger("").getHandlers())
		{
			handler.setLevel(Level.CONFIG);
		}

		GuiceContext.inject();
		GuiceContext.reflect().getSubTypesOf(GuiceDefaultBinder.class);
		GuiceContext.reflect().getSubTypesOf(GuiceSiteBinder.class);
		GuiceContext.reflect().getTypesAnnotatedWith(GuiceInjectorModuleMarker.class);
	}

	@Override
	public void onBind(GuiceInjectorModule module)
	{
		System.out.println("On Bind");
	}

	@Test
	public void testReflection()
	{
		GuiceContext.reflect();
	}

	@Test
	public void testInjection()
	{
		GuiceContext.inject();
	}

}
