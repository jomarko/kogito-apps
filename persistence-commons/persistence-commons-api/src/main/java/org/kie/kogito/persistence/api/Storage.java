package org.kie.kogito.persistence.api;

import java.util.Map;

import org.kie.kogito.persistence.api.query.Query;

import io.smallrye.mutiny.Multi;

public interface Storage<K, V> {

    /**
     * Adds a listener on the create events.
     */
    Multi<V> objectCreatedListener();

    /**
     * Adds a listener on the update events.
     */
    Multi<V> objectUpdatedListener();

    /**
     * Adds a listener on the remove events.
     */
    Multi<K> objectRemovedListener();

    /**
     * Gets the `Query` object to query the storage.
     *
     * @return The `Query` instance.
     */
    Query<V> query();

    /**
     * Gets an element by key. If the element is not present in the storage, then `null` is returned.
     *
     * @param key The key.
     * @return The element.
     */
    V get(K key);

    /**
     * Puts an element with a key. If an element with the same key is already present in the storage, then it is replaced.
     *
     * @param key The key.
     * @param value The value.
     * @return The value.
     */
    V put(K key, V value);

    /**
     * Removes an element by key. If the element is not present in the storage, then `null` is returned.
     *
     * @param key The key.
     * @return The removed object.
     */
    V remove(K key);

    /**
     * Checks whether the storage contains a key.
     *
     * @param key The key.
     * @return `true` if the key is present in the storage, `false` otherwise.
     */
    boolean containsKey(K key);

    /**
     * Gets the pair key-value entry set of the elements in the storage.
     *
     * @return The key-value pair set of the elements in the storage.
     */
    Map<K, V> entries();

    /**
     * Erase all the elements in the storage.
     */
    void clear();

    /**
     * Gets the root type for the storage.
     *
     * @return The root type for the storage.
     */
    String getRootType();
}
