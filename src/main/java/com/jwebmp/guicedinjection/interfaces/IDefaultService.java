package com.guicedee.guicedinjection.interfaces;

import com.guicedee.guicedinjection.GuiceContext;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

/**
 * Supplies standard set changer and comparable's for services
 *
 * @param <J>
 */
public interface IDefaultService<J extends IDefaultService<J>>
		extends Comparable<J>, Comparator<J> {
	/**
	 * Method loaderToSet, converts a ServiceLoader into a TreeSet
	 *
	 * @param loader of type ServiceLoader
	 * @return Set
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	static <T> Set<T> loaderToSet(ServiceLoader<T> loader) {
		Set<T> output = new TreeSet<>();
		for (T newInstance : loader) {
			output.add((T) GuiceContext.get(newInstance.getClass()));
		}
		return output;
	}

	/**
	 * Method loaderToSet, converts a ServiceLoader into a TreeSet
	 *
	 * @param loader of type ServiceLoader
	 * @return Set
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	static <T> Set<T> loaderToSetNoInjection(ServiceLoader<T> loader) {
		Set<T> output = new LinkedHashSet<>();
		for (T newInstance : loader) {
			output.add(newInstance);
		}
		return output;
	}

	/**
	 * Method compare ...
	 *
	 * @param o1 of type J
	 * @param o2 of type J
	 * @return int
	 */
	@Override
	default int compare(J o1, J o2) {
		if (o1 == null || o2 == null) {
			return -1;
		}
		return o1.sortOrder()
				 .compareTo(o2.sortOrder());
	}

	/**
	 * Default Sort Order 100
	 *
	 * @return 100
	 */
	default Integer sortOrder() {
		return 100;
	}

	/**
	 * Method compareTo ...
	 *
	 * @param o of type J
	 * @return int
	 */
	@Override
	default int compareTo(@NotNull J o) {
		int sort = sortOrder().compareTo(o.sortOrder());
		if (sort == 0) {
			return -1;
		}
		return sort;
	}


}
