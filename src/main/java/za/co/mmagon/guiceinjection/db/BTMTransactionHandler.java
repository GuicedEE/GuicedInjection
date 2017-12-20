package za.co.mmagon.guiceinjection.db;

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
		if (ut.getStatus() == Status.STATUS_NO_TRANSACTION)
		{
			ut.begin();
		}
		try
		{
			returnable = invocation.proceed();
			ut.commit();
		}
		catch (Throwable T)
		{
			ut.rollback();
			throw T;
		}
		return returnable;
	}
}
