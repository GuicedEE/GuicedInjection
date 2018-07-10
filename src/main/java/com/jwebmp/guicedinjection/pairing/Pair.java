package com.jwebmp.guicedinjection.pairing;

import javax.validation.constraints.NotNull;

/**
 * Specifies a generic pair
 *
 * @param <K>
 * 		Key
 * @param <V>
 * 		Value
 */
public class Pair<K, V>
		implements Comparable<Pair<K, V>>
{
	/**
	 * The specified key
	 */
	private K key;
	/**
	 * The specified value
	 */
	private V value;

	/**
	 * Constructs a new blank pair
	 */
	public Pair()
	{
		//Nothing Needed
	}

	/**
	 * Constructs a new key value pair
	 *
	 * @param key
	 * 		The key for the pair
	 * @param value
	 * 		The value for the pair
	 */
	@SuppressWarnings("WeakerAccess")
	public Pair(@NotNull K key, V value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "Key[" + getKey() + "]-[" + getValue() + "}";
	}

	/**
	 * Gets the key for the given pair
	 *
	 * @return The key given
	 */
	public K getKey()
	{
		return key;
	}

	/**
	 * Returns the value for the given pair
	 *
	 * @return Sets this Pairs value
	 */
	public V getValue()
	{
		return value;
	}

	/**
	 * Sets the value for the given pair
	 *
	 * @param value
	 * 		Sets this pairs values
	 *
	 * @return this Pair
	 */
	public Pair setValue(V value)
	{
		this.value = value;
		return this;
	}

	/**
	 * Sets the key for the given pair
	 *
	 * @param key
	 * 		Sets this pairs key
	 *
	 * @return The pair
	 */
	public Pair setKey(@NotNull K key)
	{
		this.key = key;
		return this;
	}

	@Override
	public int compareTo(@NotNull Pair<K, V> o)
	{
		return getKey().toString()
		               .compareTo(o.getKey()
		                           .toString());
	}
}
