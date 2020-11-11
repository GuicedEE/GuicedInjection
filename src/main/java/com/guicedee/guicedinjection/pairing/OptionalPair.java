package com.guicedee.guicedinjection.pairing;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Specifies a generic pair
 *
 * @param <K> Key
 * @param <V> Value
 */
public class OptionalPair<K, V>
		extends Pair<K, V> {

	/**
	 * Constructs a new blank pair
	 */
	public OptionalPair() {
		//No config required
	}

	/**
	 * Constructs a new key value pair
	 *
	 * @param key   The key to use
	 * @param value the value to use
	 */
	public OptionalPair(K key, V value) {
		super(key, value);
	}

	@Override
	public String toString() {
		return "Key[" + getKey() + "];Value[" + getValue() + "]";
	}

	/**
	 * Sets the value for the given pair
	 *
	 * @param value The value to set
	 * @return Optional nullable of the value
	 */
	@Override
	public OptionalPair<K, V> setValue(V value) {
		super.setValue(value);
		return this;
	}

	/**
	 * Sets the key for the given pair
	 *
	 * @param key The key to return
	 * @return The optional pair
	 */
	@Override
	public OptionalPair<K, V> setKey(@NotNull K key) {
		super.setKey(key);
		return this;
	}

	public Optional<K> getKeyOptional() {
		return Optional.ofNullable(getKey());
	}

	/**
	 * Returns the optional object of the value
	 *
	 * @return Optional nullable of the value
	 */
	public Optional<V> getValueOptional() {
		return Optional.ofNullable(getValue());
	}


}
