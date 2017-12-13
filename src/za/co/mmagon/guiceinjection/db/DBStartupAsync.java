package za.co.mmagon.guiceinjection.db;

import com.google.inject.persist.PersistService;
import za.co.mmagon.guiceinjection.GuiceContext;

import java.util.concurrent.Callable;

@DBStartup
public class DBStartupAsync
{

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
			e.printStackTrace();
		}
	}
}
