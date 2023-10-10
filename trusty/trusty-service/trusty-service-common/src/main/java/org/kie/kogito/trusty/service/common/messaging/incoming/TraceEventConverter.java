package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.tracing.event.message.MessageLevel;
import org.kie.kogito.tracing.event.trace.TraceEvent;
import org.kie.kogito.tracing.event.trace.TraceInputValue;
import org.kie.kogito.tracing.event.trace.TraceOutputValue;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionOutcome;

public class TraceEventConverter {

    private TraceEventConverter() {
    }

    public static Decision toDecision(TraceEvent event, String sourceUrl, String serviceUrl) {

        List<DecisionInput> inputs = event.getInputs() == null
                ? null
                : event.getInputs().stream().map(TraceEventConverter::toInput).collect(Collectors.toList());

        List<DecisionOutcome> outcomes = event.getOutputs() == null
                ? null
                : event.getOutputs().stream().map(TraceEventConverter::toOutcome).collect(Collectors.toList());

        return new Decision(
                event.getHeader().getExecutionId(),
                sourceUrl,
                serviceUrl,
                event.getHeader().getStartTimestamp(),
                decisionHasSucceeded(event.getOutputs()),
                null,
                event.getHeader().getResourceId().getModelName(),
                event.getHeader().getResourceId().getModelNamespace(),
                inputs,
                outcomes);
    }

    public static DecisionInput toInput(TraceInputValue eventInput) {
        return new DecisionInput(eventInput.getId(), eventInput.getName(),
                eventInput.getValue());
    }

    public static DecisionOutcome toOutcome(TraceOutputValue eventOutput) {
        List<NamedTypedValue> flattenedInputs = eventOutput.getInputs()
                .entrySet()
                .stream()
                .map(i -> new NamedTypedValue(i.getKey(), i.getValue()))
                .collect(Collectors.toList());

        return new DecisionOutcome(
                eventOutput.getId(),
                eventOutput.getName(),
                eventOutput.getStatus(),
                eventOutput.getValue(),
                flattenedInputs,
                eventOutput.getMessages());
    }

    public static boolean decisionHasSucceeded(List<TraceOutputValue> outputs) {
        return outputs != null && outputs.stream().noneMatch(o -> "failed".equalsIgnoreCase(o.getStatus()) || messageListHasErrors(o.getMessages()));
    }

    private static boolean messageListHasErrors(List<org.kie.kogito.tracing.event.message.Message> messages) {
        return messages != null && messages.stream().anyMatch(m -> m.getLevel() == MessageLevel.ERROR);
    }
}
