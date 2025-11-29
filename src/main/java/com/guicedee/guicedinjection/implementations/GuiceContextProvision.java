package com.guicedee.guicedinjection.implementations;

import com.guicedee.client.*;
import com.guicedee.client.services.IGuiceProvider;
import com.guicedee.guicedinjection.*;

public class GuiceContextProvision implements IGuiceProvider
{
	@Override
	public IGuiceContext get()
	{
		return GuiceContext.instance();
	}
	
}
