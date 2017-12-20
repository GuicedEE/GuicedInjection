package za.co.mmagon.guiceinjection.db;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.jndi.BitronixContext;
import com.google.inject.matcher.Matchers;
import com.google.inject.persist.Transactional;
import za.co.mmagon.guiceinjection.abstractions.GuiceInjectorModule;
import za.co.mmagon.guiceinjection.interfaces.GuiceDefaultBinder;

import javax.naming.NamingException;
import javax.transaction.UserTransaction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultDatabaseBinding extends GuiceDefaultBinder
{
	private static final Logger log = Logger.getLogger("Default DB");

	@Override
	public void onBind(GuiceInjectorModule module)
	{
		module.bind(UserTransaction.class).toProvider(() ->
		                                              {
			                                              BitronixContext ic = new BitronixContext();
			                                              BitronixTransactionManager btm = null;
			                                              try
			                                              {
				                                              btm = (BitronixTransactionManager) ic.lookup("java:comp/UserTransaction");
			                                              }
			                                              catch (NamingException e)
			                                              {
				                                              log.log(Level.SEVERE, "Can't find BTM user transactions", e);
			                                              }
			                                              return btm;
		                                              });

		module.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), new BTMTransactionHandler());
	}

}
