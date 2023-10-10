package org.kie.kogito.index.mongodb.model;

import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;

public class ProcessIdEntityMapper implements MongoEntityMapper<String, ProcessIdEntity> {

    static final String PROCESS_ID_ATTRIBUTE = "processId";

    @Override
    public Class<ProcessIdEntity> getEntityClass() {
        return ProcessIdEntity.class;
    }

    @Override
    public ProcessIdEntity mapToEntity(String key, String value) {
        ProcessIdEntity processIdEntity = new ProcessIdEntity();
        processIdEntity.setProcessId(key);
        processIdEntity.setFullTypeName(value);
        return processIdEntity;
    }

    @Override
    public String mapToModel(ProcessIdEntity entity) {
        return entity.getFullTypeName();
    }

    @Override
    public String convertToMongoAttribute(String attribute) {
        return PROCESS_ID_ATTRIBUTE.equals(attribute) ? MONGO_ID : MongoEntityMapper.super.convertToMongoAttribute(attribute);
    }

    @Override
    public String convertToModelAttribute(String attribute) {
        return MONGO_ID.equals(attribute) ? PROCESS_ID_ATTRIBUTE : MongoEntityMapper.super.convertToModelAttribute(attribute);
    }
}
