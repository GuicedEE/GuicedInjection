package com.jwebmp.guicedinjection.interfaces;

import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;

import javax.validation.constraints.NotNull;
import java.util.Comparator;

public interface IGuiceDefaultBinder<M extends GuiceInjectorModule>
		extends Comparable<IGuiceDefaultBinder>, Comparator<IGuiceDefaultBinder>, IDefaultBinder<M>
{
	default int compare(IGuiceDefaultBinder o1, IGuiceDefaultBinder o2)
	{
		if (o1 == null || o2 == null)
		{
			return -1;
		}
		return o1.sortOrder()
		         .compareTo(o2.sortOrder());
	}

	default Integer sortOrder()
	{
		return 100;
	}

	default int compareTo(@NotNull IGuiceDefaultBinder o)
	{
		int sort = sortOrder().compareTo(o.sortOrder());
		if (sort == 0)
		{
			return -1;
		}
		return sort;
	}

}
