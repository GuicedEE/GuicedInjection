package com.guicedee.tests;

import com.guicedee.guicedinjection.GuiceContext;

public class PsvmTest
{

	public static void main(String[] args)
	{
		GuiceContext.instance()
		            .getConfig()
		            .setServiceLoadWithClassPath(true);
		GuiceContext.inject();
	}
}
