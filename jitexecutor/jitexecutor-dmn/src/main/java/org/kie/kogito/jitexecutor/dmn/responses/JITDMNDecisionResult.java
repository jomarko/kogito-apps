package org.kie.kogito.jitexecutor.dmn.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.internal.utils.MarshallingStubUtils;

public class JITDMNDecisionResult implements Serializable,
        DMNDecisionResult {

    private String decisionId;

    private String decisionName;

    private Object result;

    private List<JITDMNMessage> messages = new ArrayList<>();

    private DecisionEvaluationStatus status;

    public JITDMNDecisionResult() {
        // Intentionally blank.
    }

    public static JITDMNDecisionResult of(DMNDecisionResult value) {
        JITDMNDecisionResult res = new JITDMNDecisionResult();
        res.decisionId = value.getDecisionId();
        res.decisionName = value.getDecisionName();
        res.setResult(value.getResult());
        res.setMessages(value.getMessages());
        res.status = value.getEvaluationStatus();
        return res;
    }

    @Override
    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }

    @Override
    public String getDecisionName() {
        return decisionName;
    }

    public void setDecisionName(String decisionName) {
        this.decisionName = decisionName;
    }

    @Override
    public DecisionEvaluationStatus getEvaluationStatus() {
        return status;
    }

    public void setEvaluationStatus(DecisionEvaluationStatus status) {
        this.status = status;
    }

    @Override
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = MarshallingStubUtils.stubDMNResult(result, String::valueOf);
    }

    public List<DMNMessage> getMessages() {
        return (List) messages;
    }

    public void setMessages(List<DMNMessage> messages) {
        this.messages = new ArrayList<>();
        for (DMNMessage m : messages) {
            this.messages.add(JITDMNMessage.of(m));
        }
    }

    @Override
    public boolean hasErrors() {
        return messages != null && messages.stream().anyMatch(m -> m.getSeverity() == DMNMessage.Severity.ERROR);
    }
}
