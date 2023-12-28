/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guicedee.guicedinjection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;



import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author GedMarc
 */
public class GuiceContextTest {

	@BeforeAll
	public static void pre() {
		//LogFactory.configureConsoleColourOutput(Level.FINE);
		GuiceContext.instance()
		            .getConfig()
		            .setServiceLoadWithClassPath(true);
	}

	@Test
	public void testInjection() {
		GuiceContext.inject();
	}

}
