package org.kie.kogito.index.event;

import java.net.URI;
import java.time.ZonedDateTime;

public abstract class AbstractBuilder<B extends AbstractBuilder<B, T, E>, T extends Object, E extends KogitoCloudEvent<T>> {

    protected E event;

    protected AbstractBuilder(E event) {
        this.event = event;
    }

    public B type(String type) {
        event.setType(type);
        return (B) this;
    }

    public B source(URI source) {
        event.setSource(source);
        return (B) this;
    }

    public B id(String id) {
        event.setId(id);
        return (B) this;
    }

    public B time(ZonedDateTime time) {
        event.setTime(time);
        return (B) this;
    }

    public B schemaURL(URI schemaURL) {
        event.setSchemaURL(schemaURL);
        return (B) this;
    }

    public B contentType(String contentType) {
        event.setContentType(contentType);
        return (B) this;
    }

    public B data(T data) {
        event.setData(data);
        return (B) this;
    }

    public B processInstanceId(String processInstanceId) {
        event.setProcessInstanceId(processInstanceId);
        return (B) this;
    }

    public B processId(String processId) {
        event.setProcessId(processId);
        return (B) this;
    }

    public B rootProcessInstanceId(String rootProcessInstanceId) {
        event.setRootProcessInstanceId(rootProcessInstanceId);
        return (B) this;
    }

    public B rootProcessId(String rootProcessId) {
        event.setRootProcessId(rootProcessId);
        return (B) this;
    }

    public E build() {
        return event;
    }
}
