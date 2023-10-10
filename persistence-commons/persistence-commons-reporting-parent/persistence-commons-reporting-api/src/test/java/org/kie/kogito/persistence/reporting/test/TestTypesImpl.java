package org.kie.kogito.persistence.reporting.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kie.kogito.persistence.reporting.database.sqlbuilders.BaseContext;
import org.kie.kogito.persistence.reporting.model.BaseField;
import org.kie.kogito.persistence.reporting.model.BaseJsonField;
import org.kie.kogito.persistence.reporting.model.BaseMapping;
import org.kie.kogito.persistence.reporting.model.BaseMappingDefinition;
import org.kie.kogito.persistence.reporting.model.BaseMappingDefinitions;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestContext;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestField;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestJsonField;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMapping;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMappingDefinition;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMappingDefinitions;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestPartitionField;

/**
 * Collection of classes extending those in the -api to hide generics for tests.
 */
public class TestTypesImpl {

    public static final class TestFieldImpl extends BaseField implements TestField {

        public TestFieldImpl(final String fieldName) {
            super(fieldName);
        }
    }

    public static final class TestJsonFieldImpl extends BaseJsonField<Object> implements TestJsonField {

        public TestJsonFieldImpl(final String fieldName,
                final Object fieldValue) {
            super(fieldName, fieldValue);
        }
    }

    public static final class TestMappingImpl extends BaseMapping<Object, TestJsonField> implements TestMapping {

        public TestMappingImpl(final String sourceJsonPath,
                final TestJsonField targetField) {
            super(sourceJsonPath,
                    targetField);
        }
    }

    public static final class TestMappingDefinitionImpl extends BaseMappingDefinition<Object, TestField, TestPartitionField, TestJsonField, TestMapping> implements TestMappingDefinition {

        public TestMappingDefinitionImpl(final String mappingId,
                final String sourceTableName,
                final String sourceTableJsonFieldName,
                final List<TestField> sourceTableIdentityFields,
                final List<TestPartitionField> sourceTablePartitionFields,
                final String targetTableName,
                final List<TestMapping> fieldMappings) {
            super(mappingId,
                    sourceTableName,
                    sourceTableJsonFieldName,
                    sourceTableIdentityFields,
                    sourceTablePartitionFields,
                    targetTableName,
                    fieldMappings);
        }
    }

    public static final class TestMappingDefinitionsImpl extends BaseMappingDefinitions<Object, TestField, TestPartitionField, TestJsonField, TestMapping, TestMappingDefinition>
            implements TestMappingDefinitions {

        public TestMappingDefinitionsImpl(final Collection<TestMappingDefinition> mappingDefinitions) {
            super(mappingDefinitions);
        }
    }

    public static final class TestContextImpl extends BaseContext<Object, TestField, TestPartitionField, TestJsonField, TestMapping> implements TestContext {

        public TestContextImpl(final String mappingId,
                final String sourceTableName,
                final String sourceTableJsonFieldName,
                final List<TestField> sourceTableIdentityFields,
                final List<TestPartitionField> sourceTablePartitionFields,
                final String targetTableName,
                final List<TestMapping> mappings,
                final List<PathSegment> mappingPaths,
                final Map<String, String> sourceTableFieldTypes) {
            super(mappingId,
                    sourceTableName,
                    sourceTableJsonFieldName,
                    sourceTableIdentityFields,
                    sourceTablePartitionFields,
                    targetTableName,
                    mappings,
                    mappingPaths,
                    sourceTableFieldTypes);
        }
    }

}
