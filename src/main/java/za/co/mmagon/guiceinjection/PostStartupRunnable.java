package za.co.mmagon.guiceinjection;

import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class PostStartupRunnable
		implements Runnable, Callable<PostStartupRunnable>, Serializable
{
	private static final long serialVersionUID = 1L;

	private transient GuicePostStartup startup;

	public PostStartupRunnable(GuicePostStartup startup)
	{
		this.startup = startup;
	}

	@Override
	public PostStartupRunnable call() throws Exception
	{
		run();
		return null;
	}

	@Override
	public void run()
	{
		startup.postLoad();
		startup = null;
	}
}
