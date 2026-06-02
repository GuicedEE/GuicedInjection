
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
