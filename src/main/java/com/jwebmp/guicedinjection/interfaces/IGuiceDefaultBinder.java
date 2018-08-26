package com.jwebmp.guicedinjection.interfaces;

import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;

public interface IGuiceDefaultBinder<J extends IGuiceDefaultBinder<J, M>, M extends GuiceInjectorModule>
		extends IDefaultService<J>, IDefaultBinder<M>
{

}
