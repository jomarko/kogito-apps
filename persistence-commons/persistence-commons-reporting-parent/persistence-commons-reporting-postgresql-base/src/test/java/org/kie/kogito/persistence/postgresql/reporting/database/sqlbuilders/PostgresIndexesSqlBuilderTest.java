package org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PostgresIndexesSqlBuilderTest extends BaseSqlBuilderImplTest {

    private final PostgresIndexesSqlBuilder indexesSqlBuilder = new PostgresIndexesSqlBuilder();

    @Override
    protected PostgresIndexesSqlBuilder getIndexesBuilder() {
        return indexesSqlBuilder;
    }

    @Override
    protected String getCreateSql(final PostgresContext context) {
        return getIndexesBuilder().createTableIndexesSql(context);
    }

    @Override
    protected String getDestroySql(final PostgresContext context) {
        return getIndexesBuilder().dropTableIndexesSql(context);
    }

    @Override
    protected void assertCreateSql(final String sql) {
        assertNotNull(sql);
        assertSequentialContent(sql,
                "CREATE INDEX idx_targetTableName_0",
                "sourceTableJsonFieldName->'root'",
                "CREATE INDEX idx_targetTableName_1",
                "sourceTableJsonFieldName->'root'->'child'",
                "CREATE INDEX idx_targetTableName_2",
                "sourceTableJsonFieldName->'root'->'child'->'collection'",
                "CREATE INDEX idx_targetTableName_3",
                "sourceTableJsonFieldName->'root'->'child'->'collection'->'child'",
                "CREATE INDEX idx_targetTableName_4",
                "sourceTableJsonFieldName->'root'->'child'->'sibling'");
    }

    @Override
    protected void assertDestroySql(final String sql) {
        assertNotNull(sql);
        assertSequentialContent(sql,
                "DROP INDEX IF EXISTS idx_targetTableName_0",
                "DROP INDEX IF EXISTS idx_targetTableName_1",
                "DROP INDEX IF EXISTS idx_targetTableName_2",
                "DROP INDEX IF EXISTS idx_targetTableName_3",
                "DROP INDEX IF EXISTS idx_targetTableName_4");
    }
}
