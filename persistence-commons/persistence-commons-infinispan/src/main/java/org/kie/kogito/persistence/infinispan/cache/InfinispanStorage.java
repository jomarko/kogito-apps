package org.kie.kogito.persistence.infinispan.cache;

import java.util.HashMap;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.infinispan.listener.CacheObjectCreatedListener;
import org.kie.kogito.persistence.infinispan.listener.CacheObjectRemovedListener;
import org.kie.kogito.persistence.infinispan.listener.CacheObjectUpdatedListener;
import org.kie.kogito.persistence.infinispan.query.InfinispanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.UnicastProcessor;

public class InfinispanStorage<K, V> implements Storage<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanStorage.class);

    private RemoteCache<K, V> delegate;
    private String rootType;

    public InfinispanStorage(RemoteCache<K, V> delegate, String rootType) {
        this.delegate = delegate;
        this.rootType = rootType;
    }

    public V get(Object key) {
        return delegate.get(key);
    }

    public void clear() {
        delegate.clear();
    }

    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return delegate.containsKey(key);
    }

    @Override
    public Map<K, V> entries() {
        return new HashMap<>(delegate);
    }

    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public Multi<V> objectCreatedListener() {
        LOGGER.debug("Adding new object created listener into Cache: {}", delegate.getName());
        UnicastProcessor<V> processor = UnicastProcessor.create();
        CacheObjectCreatedListener<K, V> listener = new CacheObjectCreatedListener<>(delegate, v -> processor.onNext(v));
        return processor
                .onSubscribe().invoke(s -> delegate.addClientListener(listener))
                .onTermination().invoke(() -> delegate.removeClientListener(listener));
    }

    @Override
    public Multi<V> objectUpdatedListener() {
        LOGGER.debug("Adding new object updated listener into Cache: {}", delegate.getName());
        UnicastProcessor<V> processor = UnicastProcessor.create();
        CacheObjectUpdatedListener<K, V> listener = new CacheObjectUpdatedListener<>(delegate, v -> processor.onNext(v));
        return processor
                .onSubscribe().invoke(s -> delegate.addClientListener(listener))
                .onTermination().invoke(() -> delegate.removeClientListener(listener));
    }

    @Override
    public Multi<K> objectRemovedListener() {
        LOGGER.debug("Adding new object removed listener into Cache: {}", delegate.getName());
        UnicastProcessor<K> processor = UnicastProcessor.create();
        CacheObjectRemovedListener<K> listener = new CacheObjectRemovedListener<>(v -> processor.onNext(v));
        return processor
                .onSubscribe().invoke(s -> delegate.addClientListener(listener))
                .onTermination().invoke(() -> delegate.removeClientListener(listener));
    }

    public RemoteCache<K, V> getDelegate() {
        return delegate;
    }

    @Override
    public String getRootType() {
        return rootType;
    }

    @Override
    public Query<V> query() {
        return new InfinispanQuery<>(delegate, rootType);
    }
}
