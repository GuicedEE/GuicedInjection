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
import za.co.mmagon.logger.LogFactory;
import za.co.mmagon.logger.handlers.ConsoleSTDOutputHandler;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author GedMarc
 */
public class GuiceContextTest
		extends GuiceDefaultBinder
{

	@BeforeAll
	public static void pre()
	{
		Handler[] handles = Logger.getLogger("")
		                          .getHandlers();
		for (Handler handle : handles)
		{
			handle.setLevel(Level.FINE);
		}
		LogFactory.setDefaultLevel(Level.FINE);
		Logger.getLogger("")
		      .addHandler(new ConsoleSTDOutputHandler(true));
	}

	@Test
	public void main()
	{
		GuiceContext.inject();
		GuiceContext.reflect()
		            .getSubTypesOf(GuiceDefaultBinder.class);
		GuiceContext.reflect()
		            .getSubTypesOf(GuiceSiteBinder.class);
		GuiceContext.reflect()
		            .getTypesAnnotatedWith(GuiceInjectorModuleMarker.class);
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
