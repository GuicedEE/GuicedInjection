package za.co.mmagon.guiceinjection.db;

import za.co.mmagon.guiceinjection.GuiceContext;
import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Initializes all classes annotated with @DBStartup
 */
@SuppressWarnings("unused")
public class DBStartupPostStartup implements GuicePostStartup
{
	private static final Logger log = Logger.getLogger("DB Initialization");

	@Override
	public void postLoad()
	{
		log.info("Starting up marked database initializers");
		Set<Class<?>> startupClasses = GuiceContext.reflect().getTypesAnnotatedWith(DBStartup.class);
		for (Class<?> clazz : startupClasses)
		{
			GuiceContext.getInstance(clazz);
		}
		log.info("All DB Startups have been called");
	}

	@Override
	public Integer sortOrder()
	{
		return 100;
	}
}
