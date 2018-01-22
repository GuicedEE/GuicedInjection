package za.co.mmagon.guiceinjection;

import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;

import java.io.Serializable;

public class PostStartupRunnable implements Runnable, Serializable {
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
}
