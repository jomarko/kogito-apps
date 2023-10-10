package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.handlers.ExplainerServiceHandlerRegistry;
import org.kie.kogito.trusty.service.common.messaging.BaseEventConsumer;
import org.kie.kogito.trusty.storage.api.StorageExceptionsProvider;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;

@ApplicationScoped
public class ExplainabilityResultConsumer extends BaseEventConsumer<BaseExplainabilityResult> {

    private static final Logger LOG = LoggerFactory.getLogger(ExplainabilityResultConsumer.class);
    private static final TypeReference<BaseExplainabilityResult> CLOUD_EVENT_TYPE = new TypeReference<>() {
    };

    private ExplainerServiceHandlerRegistry explainerServiceHandlerRegistry;

    protected ExplainabilityResultConsumer() {
        //CDI proxy
    }

    @Inject
    public ExplainabilityResultConsumer(TrustyService service,
            ExplainerServiceHandlerRegistry explainerServiceHandlerRegistry,
            ObjectMapper mapper,
            StorageExceptionsProvider storageExceptionsProvider,
            ManagedExecutor executor) {
        super(service,
                mapper,
                storageExceptionsProvider,
                executor);
        this.explainerServiceHandlerRegistry = explainerServiceHandlerRegistry;
    }

    @Override
    @Incoming("trusty-explainability-result")
    public CompletionStage<Void> handleMessage(Message<String> message) {
        return CompletableFuture.runAsync(() -> super.handleMessage(message), executor);
    }

    @Override
    protected void internalHandleCloudEvent(CloudEvent cloudEvent, BaseExplainabilityResult payload) {
        LOG.debug("CloudEvent received {}", payload);

        String executionId = payload.getExecutionId();
        Decision decision = getDecisionById(executionId);
        if (decision == null) {
            LOG.warn("Can't find decision related to explainability result (executionId={})", executionId);
        }
        service.storeExplainabilityResult(executionId, payload);
    }

    @Override
    protected TypeReference<BaseExplainabilityResult> getEventType() {
        return CLOUD_EVENT_TYPE;
    }

    protected Decision getDecisionById(String executionId) {
        try {
            return service.getDecisionById(executionId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
