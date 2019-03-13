/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jwebmp.guicedinjection;

import com.jwebmp.logger.LogFactory;
import com.jwebmp.logger.handlers.ConsoleSTDOutputHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import za.co.mmagon.externalpackage.NotEnhanceableClass;
import za.co.mmagon.externalpackage.PostConstructTestI;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author GedMarc
 */
public class GuiceContextTest
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
		GuiceContext.get(PostConstructTestI.class);
		GuiceContext.get(NotEnhanceableClass.class);

	}

	@Test
	public void testInjection()
	{
		GuiceContext.inject();
	}

}
