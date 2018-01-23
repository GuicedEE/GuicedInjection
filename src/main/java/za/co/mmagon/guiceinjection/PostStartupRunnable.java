package za.co.mmagon.guiceinjection;

import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class PostStartupRunnable implements Runnable, Callable<PostStartupRunnable>, Serializable {
	private static final long serialVersionUID = 1L;

	private GuicePostStartup startup;

	public PostStartupRunnable(GuicePostStartup startup) {
		this.startup = startup;
	}

	@Override
	public void run() {
		GuicePostStartup gps = GuiceContext.getInstance(startup.getClass());
		gps.postLoad();
		startup = null;
	}

	@Override
	public PostStartupRunnable call() throws Exception {
		run();
		return null;
	}
}
