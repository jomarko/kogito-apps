package org.kie.kogito.trusty.storage.redis;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.persistence.redis.index.RedisCreateIndexEvent;
import org.kie.kogito.persistence.redis.index.RedisIndexManager;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;
import io.redisearch.Schema;

import static org.kie.kogito.explainability.api.BaseExplainabilityRequest.EXECUTION_ID_FIELD;
import static org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest.COUNTERFACTUAL_ID_FIELD;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.COUNTERFACTUAL_REQUESTS_STORAGE;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.COUNTERFACTUAL_RESULTS_STORAGE;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.DECISIONS_STORAGE;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.LIME_RESULTS_STORAGE;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.MODELS_STORAGE;

@Singleton
@Startup
public class IndexProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexProvider.class);

    private RedisIndexManager indexManager;

    @Inject
    public IndexProvider(RedisIndexManager redisIndexManager) {
        this.indexManager = redisIndexManager;
    }

    @PostConstruct
    public void createIndexes() {
        LOGGER.debug("Creating redis indexes for Trusty Service.");
        createDecisionStorageIndex();

        createModelsStorageIndex();

        createLIMEResultsStorageIndex();

        createCounterfactualRequestsStorageIndex();

        createCounterfactualResultsStorageIndex();

        LOGGER.debug("Creation of redis indexes completed.");
    }

    private void createDecisionStorageIndex() {
        RedisCreateIndexEvent decisionIndexEvent = new RedisCreateIndexEvent(DECISIONS_STORAGE);
        decisionIndexEvent.withField(new Schema.Field(Execution.EXECUTION_ID_FIELD, Schema.FieldType.FullText, false));
        decisionIndexEvent.withField(new Schema.Field(Execution.EXECUTION_TIMESTAMP_FIELD, Schema.FieldType.Numeric, true));
        indexManager.createIndex(decisionIndexEvent);
    }

    private void createModelsStorageIndex() {
        RedisCreateIndexEvent modelIndexEvent = new RedisCreateIndexEvent(MODELS_STORAGE);
        indexManager.createIndex(modelIndexEvent);
    }

    private void createLIMEResultsStorageIndex() {
        RedisCreateIndexEvent explainabilityIndexEvent = new RedisCreateIndexEvent(LIME_RESULTS_STORAGE);
        indexManager.createIndex(explainabilityIndexEvent);
    }

    private void createCounterfactualRequestsStorageIndex() {
        RedisCreateIndexEvent counterfactualsIndexEvent = new RedisCreateIndexEvent(COUNTERFACTUAL_REQUESTS_STORAGE);
        counterfactualsIndexEvent.withField(new Schema.Field(EXECUTION_ID_FIELD, Schema.FieldType.FullText, false));
        counterfactualsIndexEvent.withField(new Schema.Field(COUNTERFACTUAL_ID_FIELD, Schema.FieldType.FullText, false));
        indexManager.createIndex(counterfactualsIndexEvent);
    }

    private void createCounterfactualResultsStorageIndex() {
        RedisCreateIndexEvent counterfactualResultsIndexEvent = new RedisCreateIndexEvent(COUNTERFACTUAL_RESULTS_STORAGE);
        counterfactualResultsIndexEvent.withField(new Schema.Field(CounterfactualExplainabilityResult.EXECUTION_ID_FIELD, Schema.FieldType.FullText, false));
        counterfactualResultsIndexEvent.withField(new Schema.Field(CounterfactualExplainabilityResult.COUNTERFACTUAL_ID_FIELD, Schema.FieldType.FullText, false));
        counterfactualResultsIndexEvent.withField(new Schema.Field(CounterfactualExplainabilityResult.COUNTERFACTUAL_SEQUENCE_ID_FIELD, Schema.FieldType.FullText, true));
        indexManager.createIndex(counterfactualResultsIndexEvent);
    }
}
