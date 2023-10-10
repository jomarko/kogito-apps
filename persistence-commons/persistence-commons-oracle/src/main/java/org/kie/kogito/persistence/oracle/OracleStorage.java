package org.kie.kogito.persistence.oracle;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.oracle.model.CacheEntity;
import org.kie.kogito.persistence.oracle.model.CacheEntityRepository;
import org.kie.kogito.persistence.oracle.model.CacheId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.mutiny.Multi;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public class OracleStorage<V> implements Storage<String, V> {

    private static final String LISTENER_NOT_AVAILABLE_IN_ORACLE_SQL = "Listener not available in Oracle";
    private CacheEntityRepository repository;
    private String cacheName;
    private Class<V> type;
    private ObjectMapper mapper;
    private String rootType;

    public OracleStorage(String cacheName, CacheEntityRepository repository, ObjectMapper mapper, Class<V> type) {
        this(cacheName, repository, mapper, type, type.getCanonicalName());
    }

    public OracleStorage(String cacheName, CacheEntityRepository repository, ObjectMapper mapper, Class<V> type, String rootType) {
        this.repository = repository;
        this.cacheName = cacheName;
        this.type = type;
        this.mapper = mapper;
        this.rootType = rootType;
    }

    @Override
    public Multi<V> objectCreatedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_ORACLE_SQL);
    }

    @Override
    public Multi<V> objectUpdatedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_ORACLE_SQL);
    }

    @Override
    public Multi<String> objectRemovedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_ORACLE_SQL);
    }

    @Override
    public Query<V> query() {
        return new OracleQuery<V>(cacheName, repository, mapper, type);
    }

    @Override
    public V get(String key) {
        return repository.findByIdOptional(new CacheId(cacheName, key)).map(mapper()).orElse(null);
    }

    protected Function<CacheEntity, V> mapper() {
        return entity -> {
            try {
                if (String.class.equals(type)) {
                    return mapper.treeToValue(entity.getValue().get("value"), type);
                } else {
                    return mapper.treeToValue(entity.getValue(), type);
                }
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(format("Failed to convert JSON into type %s", rootType), ex);
            }
        };
    }

    @Override
    public V put(String key, V value) {
        ObjectNode json;
        if (String.class.equals(type)) {
            json = mapper.createObjectNode();
            json.put("value", (String) value);
        } else {
            json = mapper.valueToTree(value);
        }
        CacheId cacheId = new CacheId(cacheName, key);
        Optional<CacheEntity> byId = repository.findByIdOptional(cacheId);
        CacheEntity entity;
        if (byId.isPresent()) {
            entity = byId.get();
        } else {
            entity = new CacheEntity(cacheName, key);
        }
        entity.setValue(json);
        repository.persist(entity);
        return value;
    }

    @Override
    public V remove(String key) {
        V value = get(key);
        if (value != null) {
            repository.deleteById(new CacheId(cacheName, key));
        }
        return value;
    }

    @Override
    public boolean containsKey(String key) {
        return repository.count("name = ?1 and key = ?2", cacheName, key) == 1;
    }

    @Override
    public Map<String, V> entries() {
        return repository.stream("name", cacheName).collect(toMap(CacheEntity::getKey, mapper()));
    }

    @Override
    public void clear() {
        repository.delete("name", cacheName);
    }

    @Override
    public String getRootType() {
        return rootType;
    }
}
