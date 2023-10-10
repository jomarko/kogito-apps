package org.kie.kogito.explainability.messaging;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class ExplainabilityMessagingHandlerCounterfactualsIT extends BaseExplainabilityMessagingHandlerIT {

    protected static final String COUNTERFACTUAL_ID = UUID.randomUUID().toString();
    protected static final String SOLUTION_ID = UUID.randomUUID().toString();
    protected static final Long MAX_RUNNING_TIME_SECONDS = 60L;

    @Override
    protected BaseExplainabilityRequest buildRequest() {
        return new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);
    }

    @Override
    protected BaseExplainabilityResult buildResult() {
        return CounterfactualExplainabilityResult.buildSucceeded(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SOLUTION_ID,
                0L,
                Boolean.TRUE,
                CounterfactualExplainabilityResult.Stage.FINAL,
                Collections.emptyList(),
                Collections.emptyList());
    }

    @Override
    protected void assertResult(BaseExplainabilityResult result) {
        assertTrue(result instanceof CounterfactualExplainabilityResult);
    }

    @Override
    protected int getTotalExpectedEventCountWithIntermediateResults() {
        return 2;
    }

    @Override
    protected void mockExplainAsyncInvocationWithIntermediateResults(Consumer<BaseExplainabilityResult> callback) {
        CounterfactualExplainabilityResult intermediateResult = CounterfactualExplainabilityResult.buildSucceeded(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SOLUTION_ID,
                0L,
                Boolean.TRUE,
                CounterfactualExplainabilityResult.Stage.INTERMEDIATE,
                Collections.emptyList(),
                Collections.emptyList());
        callback.accept(intermediateResult);
    }
}
