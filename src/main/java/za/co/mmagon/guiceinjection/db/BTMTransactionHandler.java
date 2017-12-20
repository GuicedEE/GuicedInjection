package za.co.mmagon.guiceinjection.db;

import com.google.inject.persist.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import za.co.mmagon.guiceinjection.GuiceContext;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

public class BTMTransactionHandler implements MethodInterceptor
{
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable
	{
		UserTransaction ut = null;
		try
		{
			ut = GuiceContext.getInstance(UserTransaction.class);
		}
		catch (Exception T)
		{
			return invocation.proceed();
		}

		Object returnable = null;
		if (ut.getStatus() == Status.STATUS_NO_TRANSACTION || ut.getStatus() != Status.STATUS_ACTIVE)
		{
			ut.begin();
		}
		try
		{
			returnable = invocation.proceed();
			ut.commit();
		}
		catch (IllegalStateException ise)
		{
			//Nothing to commit in this transaction
		}
		catch (Throwable T)
		{
			Transactional t = invocation.getMethod().getAnnotation(Transactional.class);
			for (Class<? extends Exception> aClass : t.rollbackOn())
			{
				if (aClass.isAssignableFrom(T.getClass()))
				{
					ut.rollback();
				}
			}
		}
		return returnable;
	}
}
