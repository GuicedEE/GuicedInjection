package com.guicedee.guicedinjection.interfaces;

import com.guicedee.guicedinjection.abstractions.GuiceInjectorModule;

public interface IGuiceDefaultBinder<J extends IGuiceDefaultBinder<J, M>, M extends GuiceInjectorModule>
		extends IDefaultService<J>, IDefaultBinder<M> {

}
