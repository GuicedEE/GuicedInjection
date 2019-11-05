package com.guicedee.guicedinjection;

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
