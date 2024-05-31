package com.guicedee.tests;

import com.guicedee.guicedinjection.interfaces.IGuicePostStartup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ParallelPostStartupTest1 implements IGuicePostStartup<ParallelPostStartupTest1>
{
	@Override
	public List<CompletableFuture<Boolean>> postLoad()
	{
		return List.of(CompletableFuture.supplyAsync(() -> {
			System.out.println("Starting 1");
			return true;
		}));
	}
	
	@Override
	public Integer sortOrder()
	{
		return 200;
	}
}
