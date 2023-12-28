package com.guicedee.guicedinjection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class ExecutorServiceSupplier implements Supplier<ExecutorService>
{
	@Override
	public ExecutorService get()
	{
		return Executors.newCachedThreadPool();
	}
}
