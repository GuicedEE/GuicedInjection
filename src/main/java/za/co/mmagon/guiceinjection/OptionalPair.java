package za.co.mmagon.guiceinjection;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Specifies a generic pair
 *
 * @param <K>
 * 		Key
 * @param <V>
 * 		Value
 */
public class OptionalPair<K, V>
		extends Pair<K, V>
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
		key = Optional.empty();
		value = Optional.empty();
	}

	/**
	 * Constructs a new key value pair
	 *
	 * @param key
	 * @param value
	 */
	public OptionalPair(@Nullable K key, @Nullable V value)
	{
		this.key = Optional.ofNullable(key);
		this.value = Optional.ofNullable(value);
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
	public OptionalPair<K, V> setKey(@Nullable K key)
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
	public OptionalPair<K, V> setValue(@Nullable V value)
	{
		this.value = Optional.of(value);
		return this;
	}

	public Optional<K> getKeyOptional()
	{
		return key;
	}

	public Optional<V> getValueOptional()
	{
		return value;
	}

}
