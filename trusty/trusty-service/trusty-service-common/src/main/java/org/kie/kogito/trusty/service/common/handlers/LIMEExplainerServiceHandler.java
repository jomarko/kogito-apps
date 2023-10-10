package org.kie.kogito.trusty.service.common.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LIMEExplainerServiceHandler extends BaseExplainerServiceHandler<LIMEExplainabilityResult> {

    private static final Logger LOG = LoggerFactory.getLogger(LIMEExplainerServiceHandler.class);

    protected LIMEExplainerServiceHandler() {
        //CDI proxy
    }

    @Inject
    public LIMEExplainerServiceHandler(TrustyStorageService storageService) {
        super(storageService);
    }

    @Override
    public <T extends BaseExplainabilityResult> boolean supports(Class<T> type) {
        return LIMEExplainabilityResult.class.isAssignableFrom(type);
    }

    @Override
    public LIMEExplainabilityResult getExplainabilityResultById(String executionId) {
        Storage<String, LIMEExplainabilityResult> storage = storageService.getLIMEResultStorage();
        if (!storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("A LIME result for Execution ID '%s' does not exist in the LIME results storage.", executionId));
        }
        return storage.get(executionId);
    }

    @Override
    public void storeExplainabilityResult(String executionId, LIMEExplainabilityResult result) {
        Storage<String, LIMEExplainabilityResult> storage = storageService.getLIMEResultStorage();
        if (storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("A LIME result for Execution ID '%s' is already present in the LIME results storage.", executionId));
        }
        storage.put(executionId, result);
        LOG.info("Stored LIME explainability result for execution {}", executionId);
    }
}
