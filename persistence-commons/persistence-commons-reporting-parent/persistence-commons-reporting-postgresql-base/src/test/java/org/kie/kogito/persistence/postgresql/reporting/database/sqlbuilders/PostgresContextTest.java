package org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostgresContextTest {

    @Test
    void testPostgresContext() {
        final PostgresField field = new PostgresField("field1");
        final PostgresPartitionField partitionField = new PostgresPartitionField("partitionField1", "value");
        final PostgresMapping mapping = new PostgresMapping("sourceJsonPath",
                new PostgresJsonField("targetField1", JsonType.STRING));
        final PathSegment pathSegment = new PathSegment("segment", null);
        final Map<String, String> sourceTableFieldTypes = Map.of("field1", "text");

        final PostgresContext context = new PostgresContext("mappingId",
                "sourceTableName",
                "sourceTableJsonFieldName",
                List.of(field),
                List.of(partitionField),
                "targetTableName",
                List.of(mapping),
                List.of(pathSegment),
                sourceTableFieldTypes);

        assertEquals("mappingId",
                context.getMappingId());
        assertEquals("sourceTableName",
                context.getSourceTableName());
        assertEquals("sourceTableJsonFieldName",
                context.getSourceTableJsonFieldName());
        assertEquals(List.of(field),
                context.getSourceTableIdentityFields());
        assertEquals(List.of(partitionField),
                context.getSourceTablePartitionFields());
        assertEquals("targetTableName",
                context.getTargetTableName());
        assertEquals(List.of(mapping),
                context.getFieldMappings());
        assertEquals(List.of(pathSegment),
                context.getMappingPaths());
        assertEquals(sourceTableFieldTypes,
                context.getSourceTableFieldTypes());
    }
}
