/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guicedee.guicedinjection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.guicedee.logger.LogFactory;
import com.guicedee.logger.handlers.ConsoleSTDOutputHandler;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author GedMarc
 */
public class GuiceContextTest {

	@BeforeAll
	public static void pre() {
		Handler[] handles = Logger.getLogger("")
								  .getHandlers();
		for (Handler handle : handles) {
			handle.setLevel(Level.FINE);
		}
		LogFactory.setDefaultLevel(Level.FINE);
		Logger.getLogger("")
			  .addHandler(ConsoleSTDOutputHandler.getInstance()
												 .setColoured(true));
	}

	@Test
	public void testInjection() {
		GuiceContext.inject();
	}


}
