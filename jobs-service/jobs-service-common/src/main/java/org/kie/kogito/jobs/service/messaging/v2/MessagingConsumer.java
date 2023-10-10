package org.kie.kogito.jobs.service.messaging.v2;

import java.util.Objects;

import org.kie.kogito.jobs.service.adapter.JobDetailsAdapter;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.event.serialization.JobCloudEventDeserializer;
import org.kie.kogito.jobs.service.messaging.ReactiveMessagingEventConsumer;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;

public class MessagingConsumer extends ReactiveMessagingEventConsumer {

    protected JobCloudEventDeserializer deserializer;

    public MessagingConsumer() {
    }

    public MessagingConsumer(TimerDelegateJobScheduler scheduler, ReactiveJobRepository jobRepository, ObjectMapper objectMapper) {
        super(scheduler, jobRepository, CreateJobEvent.TYPE, DeleteJobEvent.TYPE);
        this.deserializer = new JobCloudEventDeserializer(objectMapper);
    }

    @Override
    public JobDetails getJobDetails(CloudEvent createEvent) {
        if (!Objects.equals(getCreateJobEventType(), createEvent.getType())) {
            throw new IllegalArgumentException("Only " + getCreateJobEventType() + "is supported to get JobDetails " + createEvent);
        }
        final CreateJobEvent jobCloudEvent = (CreateJobEvent) deserializer.deserialize(createEvent);
        return JobDetailsAdapter.from(jobCloudEvent.getData());
    }

    @Override
    public String getJobId(CloudEvent createEvent) {
        if (!Objects.equals(getCancelJobEventType(), createEvent.getType())) {
            throw new IllegalArgumentException("Only " + getCreateJobEventType() + "is supported to get Job Id " + createEvent);
        }
        final DeleteJobEvent jobCloudEvent = (DeleteJobEvent) deserializer.deserialize(createEvent);
        return jobCloudEvent.getData().getId();
    }
}
