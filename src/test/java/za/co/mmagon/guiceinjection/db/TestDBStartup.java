package za.co.mmagon.guiceinjection.db;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

import javax.sql.DataSource;

public class TestDBStartup extends DBStartupAsync
{
	@Inject
	public TestDBStartup(@TestCustomPersistenceLoader PersistService ps, @TestCustomPersistenceLoader DataSource ds)
	{
		ps.start();
	}
}
