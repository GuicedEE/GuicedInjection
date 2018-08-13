package com.jwebmp.guicedinjection.interfaces;

import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

public interface IGuiceDefaultBinder<M extends GuiceInjectorModule>
		extends Comparable<IGuiceDefaultBinder>, Comparator<IGuiceDefaultBinder>, IDefaultBinder<M>
{
	@Override
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

	@Override
	default int compareTo(@NotNull IGuiceDefaultBinder o)
	{
		int sort = sortOrder().compareTo(o.sortOrder());
		if (sort == 0)
		{
			return -1;
		}
		return sort;
	}

	/**
	 * Method loaderToSet, converts a ServiceLoader into a TreeSet
	 *
	 * @param loader
	 * 		of type ServiceLoader<T>
	 *
	 * @return Set<T>
	 */
	@SuppressWarnings("MissingClassJavaDoc")
	default <T> Set<T> loaderToSet(ServiceLoader<T> loader)
	{
		Set<T> output = new TreeSet<>();
		for (T newInstance : loader)
		{
			//noinspection unchecked
			output.add((T) GuiceContext.get(newInstance.getClass()));
		}
		return output;
	}
}
