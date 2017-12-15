package za.co.mmagon.guiceinjection.db;

import com.google.inject.persist.PersistService;
import za.co.mmagon.guiceinjection.GuiceContext;
import za.co.mmagon.guiceinjection.annotations.DBStartup;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@DBStartup
public class DBStartupAsync
{
	private static final Logger log = Logger.getLogger("DBStartupAsync");

	protected void buildStartupThread(PersistService persistService)
	{
		Callable asyncStartup = () ->
		{
			persistService.start();
			return true;
		};

		try
		{
			GuiceContext.getAsynchronousFileLoader().submit(asyncStartup);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to submit the execution thread for startup", e);
		}
	}
}
