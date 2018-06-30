package com.jwebmp.guicedinjection;

import com.jwebmp.guicedinjection.annotations.GuicePostStartup;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * A thread type future or current to startup asynchronously during post startup execution.
 * Executed internally.
 */
class PostStartupRunnable
		implements Runnable, Callable<PostStartupRunnable>, Serializable
{
	private static final long serialVersionUID = 1L;

	private transient GuicePostStartup startup;

	public PostStartupRunnable(GuicePostStartup startup)
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
