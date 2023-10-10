package org.kie.kogito.index.mongodb.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.index.mongodb.model.ProcessIdEntityMapper.PROCESS_ID_ATTRIBUTE;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;

class ProcessIdEntityMapperTest {

    ProcessIdEntityMapper processIdEntityMapper = new ProcessIdEntityMapper();

    @Test
    void testGetEntityClass() {
        assertEquals(ProcessIdEntity.class, processIdEntityMapper.getEntityClass());
    }

    @Test
    void testMapToEntity() {
        String testId = "testProcessId";
        String testValue = "testProcessType";
        ProcessIdEntity result = processIdEntityMapper.mapToEntity(testId, testValue);

        ProcessIdEntity processIdEntity = new ProcessIdEntity();
        processIdEntity.setProcessId(testId);
        processIdEntity.setFullTypeName(testValue);

        assertEquals(processIdEntity, result);
    }

    @Test
    void testMapToModel() {
        String testId = "testProcessId";
        String testValue = "testProcessType";

        ProcessIdEntity processIdEntity = new ProcessIdEntity();
        processIdEntity.setProcessId(testId);
        processIdEntity.setFullTypeName(testValue);

        String result = processIdEntityMapper.mapToModel(processIdEntity);

        assertEquals(testValue, result);
    }

    @Test
    void testConvertToMongoAttribute() {
        assertEquals(MONGO_ID, processIdEntityMapper.convertToMongoAttribute(PROCESS_ID_ATTRIBUTE));

        String testAttribute = "testAttribute";
        assertEquals(testAttribute, processIdEntityMapper.convertToMongoAttribute(testAttribute));
    }

    @Test
    void testConvertToModelAttribute() {
        assertEquals(PROCESS_ID_ATTRIBUTE, processIdEntityMapper.convertToModelAttribute(MONGO_ID));

        String testAttribute = "test.attribute.name";
        assertEquals("name", processIdEntityMapper.convertToModelAttribute(testAttribute));
    }
}