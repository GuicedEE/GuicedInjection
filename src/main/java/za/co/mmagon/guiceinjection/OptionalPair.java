package za.co.mmagon.guiceinjection;

import java.util.Optional;

/**
 * Specifies a generic pair
 *
 * @param <K>
 * 		Key
 * @param <V>
 * 		Value
 */
public class OptionalPair<K, V> extends Pair<K, V>
{
	/**
	 * The specified key
	 */
	private Optional<K> key;
	/**
	 * The specified value
	 */
	private Optional<V> value;

	/**
	 * Constructs a new blank pair
	 */
	public OptionalPair()
	{
		//Nothing needed
	}

	/**
	 * Constructs a new key value pair
	 *
	 * @param key
	 * @param value
	 */
	public OptionalPair(K key, V value)
	{
		this.key = Optional.of(key);
		this.value = Optional.of(value);
	}

	@Override
	public String toString()
	{
		return "Key[" + getKey() + "];Value[" + getValue() + "]";
	}

	/**
	 * Gets the key for the given pair
	 *
	 * @return
	 */
	@Override
	public K getKey()
	{
		return key.get();
	}

	/**
	 * Sets the key for the given pair
	 *
	 * @param key
	 *
	 * @return
	 */
	@Override
	public OptionalPair setKey(K key)
	{
		this.key = Optional.of(key);
		return this;
	}

	/**
	 * Returns the value for the given pair
	 *
	 * @return
	 */
	@Override
	public V getValue()
	{
		return value.get();
	}

	/**
	 * Sets the value for the given pair
	 *
	 * @param value
	 *
	 * @return
	 */
	@Override
	public OptionalPair setValue(V value)
	{
		this.value = Optional.of(value);
		return this;
	}
}
