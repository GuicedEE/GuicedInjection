package com.guicedee.guicedinjection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Supplies a new {@link ExecutorService} backed by virtual threads.
 */
public class ExecutorServiceSupplier implements Supplier<ExecutorService>
{
	/**
	 * Creates a new virtual-thread-per-task executor.
	 *
	 * @return a fresh {@link ExecutorService} instance
	 */
	@Override
	public ExecutorService get()
	{
		return Executors.newVirtualThreadPerTaskExecutor();
	}
}
