package com.guicedee.guicedinjection.implementations;

import com.guicedee.client.services.IJobService;
import com.guicedee.client.services.IJobServiceProvider;
import com.guicedee.guicedinjection.*;

public class JobServiceProvision implements IJobServiceProvider
{
	@Override
	public IJobService get()
	{
		return JobService.INSTANCE;
	}
	
}
