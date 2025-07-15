/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guicedee.tests;

import com.guicedee.client.*;
import com.guicedee.guicedinjection.GuiceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
		IGuiceContext
				.getContext().inject();
	}

}
