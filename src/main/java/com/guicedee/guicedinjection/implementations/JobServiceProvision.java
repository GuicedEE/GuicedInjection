package com.guicedee.guicedinjection.implementations;

import com.guicedee.guicedinjection.*;
import com.guicedee.guicedinjection.interfaces.*;

public class JobServiceProvision implements IJobServiceProvider
{
	@Override
	public IJobService get()
	{
		return JobService.INSTANCE;
	}
	
}
