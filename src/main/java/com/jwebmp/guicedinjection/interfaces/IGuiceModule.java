package com.jwebmp.guicedinjection.interfaces;

import javax.validation.constraints.NotNull;
import java.util.Comparator;

/**
 * Service Locator for configuring the module
 */
public interface IGuiceModule extends Comparable<IGuiceModule>, Comparator<IGuiceModule>
{
	default int compare(IGuiceModule o1, IGuiceModule o2)
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

	default int compareTo(@NotNull IGuiceModule o)
	{
		int sort = sortOrder().compareTo(o.sortOrder());
		if (sort == 0)
		{
			return -1;
		}
		return sort;
	}
}
