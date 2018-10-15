package com.jwebmp.guicedinjection.threading;

import com.jwebmp.guicedinjection.interfaces.IGuicePostStartup;

import java.util.concurrent.Callable;

/**
 * A thread type future or current to startup asynchronously during post startup execution.
 * Executed internally.
 */
public class PostStartupRunnable
		implements Runnable, Callable<PostStartupRunnable>
{
	private transient IGuicePostStartup startup;

	public PostStartupRunnable(IGuicePostStartup startup)
	{
		this.startup = startup;
	}

	@Override
	public PostStartupRunnable call()
	{
		run();
		return this;
	}

	@Override
	public void run()
	{
		startup.postLoad();
		startup = null;
	}
}
