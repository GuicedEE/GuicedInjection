package com.guicedee.guicedinjection.implementations;

import com.guicedee.client.*;
import com.guicedee.guicedinjection.*;
import com.guicedee.guicedinjection.interfaces.*;

public class GuiceContextProvision implements IGuiceProvider
{
	@Override
	public IGuiceContext get()
	{
		return GuiceContext.instance();
	}
	
}
