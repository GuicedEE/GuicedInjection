package com.guicedee.tests;

import com.guicedee.guicedinjection.interfaces.IGuicePostStartup;

public class ParallelPostStartupTest2 implements IGuicePostStartup<ParallelPostStartupTest2>
{
	@Override
	public void postLoad()
	{
		System.out.println("Starting 2");
	}
	
	@Override
	public Integer sortOrder()
	{
		return 200;
	}
}
