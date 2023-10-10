package org.kie.kogito.index.addon.graphql;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.index.graphql.AbstractGraphQLSchemaManager;
import org.kie.kogito.index.model.ProcessInstanceState;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;

@ApplicationScoped
public class GraphQLAddonSchemaManagerImpl extends AbstractGraphQLSchemaManager {

    public GraphQLSchema createSchema() {
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        typeDefinitionRegistry.merge(loadSchemaDefinitionFile("basic.schema.graphqls"));

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> {
                    builder.dataFetcher("ProcessDefinitions", this::getProcessDefinitionsValues);
                    builder.dataFetcher("ProcessInstances", this::getProcessInstancesValues);
                    builder.dataFetcher("UserTaskInstances", this::getUserTaskInstancesValues);
                    builder.dataFetcher("Jobs", this::getJobsValues);
                    return builder;
                })
                .type("Mutation", builder -> {
                    builder.dataFetcher("ProcessInstanceAbort", this::abortProcessInstance);
                    builder.dataFetcher("ProcessInstanceRetry", this::retryProcessInstance);
                    builder.dataFetcher("ProcessInstanceSkip", this::skipProcessInstance);
                    builder.dataFetcher("ProcessInstanceUpdateVariables", this::updateProcessInstanceVariables);
                    builder.dataFetcher("NodeInstanceTrigger", this::triggerNodeInstance);
                    builder.dataFetcher("NodeInstanceRetrigger", this::retriggerNodeInstance);
                    builder.dataFetcher("NodeInstanceCancel", this::cancelNodeInstance);
                    builder.dataFetcher("JobCancel", this::cancelJob);
                    builder.dataFetcher("JobReschedule", this::rescheduleJob);
                    return builder;
                })
                .type("ProcessDefinition", builder -> {
                    builder.dataFetcher("source", e -> getProcessDefinitionSource(e.getSource()));
                    builder.dataFetcher("nodes", e -> getProcessDefinitionNodes(e.getSource()));
                    builder.dataFetcher("serviceUrl", this::getProcessDefinitionServiceUrl);
                    return builder;
                })
                .type("ProcessInstance", builder -> {
                    builder.dataFetcher("parentProcessInstance", this::getParentProcessInstanceValue);
                    builder.dataFetcher("childProcessInstances", this::getChildProcessInstancesValues);
                    builder.dataFetcher("serviceUrl", this::getProcessInstanceServiceUrl);
                    builder.dataFetcher("diagram", this::getProcessInstanceDiagram);
                    builder.dataFetcher("source", this::getProcessInstanceSource);
                    builder.dataFetcher("nodeDefinitions", this::getProcessInstanceNodes);
                    builder.dataFetcher("definition", this::getProcessDefinition);
                    return builder;
                })
                .type("ProcessInstanceState", builder -> {
                    builder.enumValues(name -> ProcessInstanceState.valueOf(name).ordinal());
                    return builder;
                })
                .scalar(getDateTimeScalarType())
                .scalar(ExtendedScalars.GraphQLBigDecimal)
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.Json)
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

}
