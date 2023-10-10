package org.kie.kogito.explainability.handlers;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequest;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.PredictionProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalExplainerServiceHandlerRegistryTest {

    private static final String EXECUTION_ID = "executionId";

    private static final String SERVICE_URL = "serviceURL";

    private static final ModelIdentifier MODEL_IDENTIFIER = new ModelIdentifier("resourceType", "resourceId");

    private static final String COUNTERFACTUAL_ID = "counterfactualId";

    private static final Long MAX_RUNNING_TIME_SECONDS = 60L;

    private LimeExplainerServiceHandler limeExplainerServiceHandler;
    private CounterfactualExplainerServiceHandler counterfactualExplainerServiceHandler;
    private PredictionProvider predictionProvider;
    private Consumer<BaseExplainabilityResult> callback;

    private LocalExplainerServiceHandlerRegistry registry;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        LimeExplainer limeExplainer = mock(LimeExplainer.class);
        CounterfactualExplainer counterfactualExplainer = mock(CounterfactualExplainer.class);
        PredictionProviderFactory predictionProviderFactory = mock(PredictionProviderFactory.class);
        limeExplainerServiceHandler = spy(new LimeExplainerServiceHandler(limeExplainer,
                predictionProviderFactory));
        counterfactualExplainerServiceHandler = spy(new CounterfactualExplainerServiceHandler(counterfactualExplainer,
                predictionProviderFactory,
                MAX_RUNNING_TIME_SECONDS));
        predictionProvider = mock(PredictionProvider.class);
        callback = mock(Consumer.class);

        when(predictionProviderFactory.createPredictionProvider(any(), any(), any())).thenReturn(predictionProvider);
        Instance<LocalExplainerServiceHandler<?, ?>> explanationHandlers = mock(Instance.class);
        when(explanationHandlers.stream()).thenReturn(Stream.of(limeExplainerServiceHandler, counterfactualExplainerServiceHandler));
        registry = new LocalExplainerServiceHandlerRegistry(explanationHandlers);
    }

    @Test
    public void testLIME_explainAsyncWithResults() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyList(),
                Collections.emptyList());

        registry.explainAsyncWithResults(request, callback);

        verify(limeExplainerServiceHandler).explainAsyncWithResults(eq(request), eq(callback));
    }

    @Test
    public void testCounterfactual_explainAsyncWithResults() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        registry.explainAsyncWithResults(request, callback);

        verify(counterfactualExplainerServiceHandler).explainAsyncWithResults(eq(request), eq(callback));
    }

}
