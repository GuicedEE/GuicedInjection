package com.guicedee.tests;

import com.guicedee.client.*;
import com.guicedee.guicedinjection.GuiceContext;

public class PsvmTest
{

	public static void main(String[] args)
	{
		GuiceContext.instance()
		            .getConfig()
		            .setServiceLoadWithClassPath(true);
		IGuiceContext
				.getContext().inject();
	}
}
