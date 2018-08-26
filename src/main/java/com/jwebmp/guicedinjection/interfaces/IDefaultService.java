package com.jwebmp.guicedinjection.interfaces;

import com.jwebmp.guicedinjection.GuiceContext;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

public interface IDefaultService<J extends IDefaultService<J>>
		extends Comparable<J>, Comparator<J>
{
	/**
	 * Method loaderToSet, converts a ServiceLoader into a TreeSet
	 *
	 * @param loader
	 * 		of type ServiceLoader<T>
	 *
	 * @return Set<T>
	 */
	@SuppressWarnings("MissingClassJavaDoc")
	static <T> Set<T> loaderToSet(ServiceLoader<T> loader)
	{
		Set<T> output = new TreeSet<>();
		for (T newInstance : loader)
		{
			//noinspection unchecked
			output.add((T) GuiceContext.get(newInstance.getClass()));
		}
		return output;
	}

	@Override
	default int compare(J o1, J o2)
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
	default int compareTo(@NotNull J o)
	{
		int sort = sortOrder().compareTo(o.sortOrder());
		if (sort == 0)
		{
			return -1;
		}
		return sort;
	}
}
