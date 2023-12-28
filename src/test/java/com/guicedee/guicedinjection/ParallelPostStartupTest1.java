package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.interfaces.IGuicePostStartup;

public class ParallelPostStartupTest1 implements IGuicePostStartup<ParallelPostStartupTest1>
{
	@Override
	public void postLoad()
	{
		System.out.println("Starting 1");
	}
	
	@Override
	public Integer sortOrder()
	{
		return 200;
	}
}
