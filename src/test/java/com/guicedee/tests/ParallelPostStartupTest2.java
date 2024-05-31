package com.guicedee.tests;

import com.guicedee.guicedinjection.interfaces.IGuicePostStartup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ParallelPostStartupTest2 implements IGuicePostStartup<ParallelPostStartupTest2>
{
	@Override
	public List<CompletableFuture<Boolean>> postLoad()
	{
		return List.of(CompletableFuture.supplyAsync(() -> {
			System.out.println("Starting 2");
			return true;
		}));
	}
	
	@Override
	public Integer sortOrder()
	{
		return 200;
	}
}
