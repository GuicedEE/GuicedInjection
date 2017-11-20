package com.armineasy.injection;

/**
 * Specifies a generic pair
 *
 * @param <K>
 * 		Key
 * @param <V>
 * 		Value
 */
public class Pair<K, V>
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
	 * @param value
	 */
	public Pair(K key, V value)
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
	 * @return
	 */
	public K getKey()
	{
		return key;
	}

	/**
	 * Sets the key for the given pair
	 *
	 * @param key
	 *
	 * @return
	 */
	public Pair setKey(K key)
	{
		this.key = key;
		return this;
	}

	/**
	 * Returns the value for the given pair
	 *
	 * @return
	 */
	public V getValue()
	{
		return value;
	}

	/**
	 * Sets the value for the given pair
	 *
	 * @param value
	 *
	 * @return
	 */
	public Pair setValue(V value)
	{
		this.value = value;
		return this;
	}
}
