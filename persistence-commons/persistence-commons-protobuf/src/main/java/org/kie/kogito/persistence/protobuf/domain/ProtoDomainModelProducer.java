package org.kie.kogito.persistence.protobuf.domain;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.impl.AnnotatedDescriptorImpl;
import org.kie.kogito.persistence.api.proto.AttributeDescriptor;
import org.kie.kogito.persistence.api.proto.DomainDescriptor;
import org.kie.kogito.persistence.api.proto.DomainModelRegisteredEvent;
import org.kie.kogito.persistence.protobuf.FileDescriptorRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class ProtoDomainModelProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoDomainModelProducer.class);

    @Inject
    Event<DomainModelRegisteredEvent> domainEvent;

    public void onFileDescriptorRegistered(@Observes FileDescriptorRegisteredEvent event) {
        FileDescriptor descriptor = event.getDescriptor();
        String rootMessage = (String) descriptor.getOption("kogito_model").getValue();
        String processId = (String) descriptor.getOption("kogito_id").getValue();

        Map<String, Descriptor> map = descriptor.getMessageTypes().stream().collect(toMap(AnnotatedDescriptorImpl::getName, desc -> desc));

        Descriptor rootDescriptor = map.remove(rootMessage);

        DomainDescriptor domain = new DomainDescriptorMapper().apply(rootDescriptor);

        List<DomainDescriptor> additionalTypes = map.values().stream().map(desc -> new DomainDescriptorMapper().apply(desc)).collect(toList());

        domainEvent.fire(new DomainModelRegisteredEvent(processId, domain, additionalTypes));
    }

    private class FieldDescriptorMapper implements Function<FieldDescriptor, AttributeDescriptor> {

        @Override
        public AttributeDescriptor apply(FieldDescriptor field) {
            return new AttributeDescriptor(field.getName(), new FieldTypeMapper().apply(field), field.getLabel().toString());
        }
    }

    private class DomainDescriptorMapper implements Function<Descriptor, DomainDescriptor> {

        @Override
        public DomainDescriptor apply(Descriptor descriptor) {
            DomainDescriptor domain = new DomainDescriptor();
            LOGGER.debug("Mapping domain from message, type: {}", descriptor.getFullName());
            domain.setTypeName(descriptor.getFullName());
            domain.setAttributes(descriptor.getFields().stream().map(fd -> new FieldDescriptorMapper().apply(fd)).collect(toList()));
            return domain;
        }
    }

    private class FieldTypeMapper implements Function<FieldDescriptor, String> {

        @Override
        public String apply(FieldDescriptor fd) {
            switch (fd.getJavaType()) {
                case INT:
                    return Integer.class.getName();
                case LONG:
                    return Long.class.getName();
                case FLOAT:
                    return Float.class.getName();
                case DOUBLE:
                    return Double.class.getName();
                case BOOLEAN:
                    return Boolean.class.getName();
                case MESSAGE:
                    if (fd.getOption("kogito_java_class") != null) {
                        return fd.getOption("kogito_java_class").getValue().toString();
                    }
                    return fd.getMessageType().getFullName();
                default:
                    return String.class.getName();
            }
        }
    }

}
