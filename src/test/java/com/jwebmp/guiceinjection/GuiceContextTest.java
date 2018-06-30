/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jwebmp.guiceinjection;

import com.jwebmp.guiceinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guiceinjection.annotations.GuiceInjectorModuleMarker;
import com.jwebmp.guiceinjection.interfaces.GuiceDefaultBinder;
import com.jwebmp.logger.LogFactory;
import com.jwebmp.logger.handlers.ConsoleSTDOutputHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
		      .addHandler(ConsoleSTDOutputHandler.getInstance()
		                                         .setColoured(true));
	}

	@Test
	public void main()
	{
		GuiceContext.inject();
		GuiceContext.reflect()
		            .getSubTypesOf(GuiceDefaultBinder.class);
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
