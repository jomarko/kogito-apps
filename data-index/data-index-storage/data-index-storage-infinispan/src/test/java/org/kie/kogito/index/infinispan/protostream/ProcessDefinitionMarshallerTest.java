package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;
import java.util.HashSet;

import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessDefinition;
import org.mockito.InOrder;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.ADDONS;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.ID;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.NAME;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.ROLES;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.TYPE;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.VERSION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProcessDefinitionMarshallerTest {

    @Test
    void testReadFrom() throws IOException {
        MessageMarshaller.ProtoStreamReader reader = mock(MessageMarshaller.ProtoStreamReader.class);
        when(reader.readString(ID)).thenReturn("processId");
        when(reader.readString(VERSION)).thenReturn("1.0");
        when(reader.readString(NAME)).thenReturn("processName");
        when(reader.readCollection(eq(ROLES), any(), eq(String.class))).thenReturn(new HashSet<>(singleton("admin")));
        when(reader.readCollection(eq(ADDONS), any(), eq(String.class))).thenReturn(new HashSet<>(singleton("process-management")));
        when(reader.readString(TYPE)).thenReturn("processType");

        ProcessDefinitionMarshaller marshaller = new ProcessDefinitionMarshaller(null);
        ProcessDefinition pd = marshaller.readFrom(reader);

        assertThat(pd)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID, "processId")
                .hasFieldOrPropertyWithValue(VERSION, "1.0")
                .hasFieldOrPropertyWithValue(NAME, "processName")
                .hasFieldOrPropertyWithValue(ROLES, singleton("admin"))
                .hasFieldOrPropertyWithValue(ADDONS, singleton("process-management"))
                .hasFieldOrPropertyWithValue(TYPE, "processType");

        InOrder inOrder = inOrder(reader);
        inOrder.verify(reader).readString(ID);
        inOrder.verify(reader).readString(VERSION);
        inOrder.verify(reader).readString(NAME);
        inOrder.verify(reader).readCollection(ROLES, new HashSet<>(), String.class);
        inOrder.verify(reader).readCollection(ADDONS, new HashSet<>(), String.class);
        inOrder.verify(reader).readString(TYPE);
    }

    @Test
    void testWriteTo() throws IOException {
        ProcessDefinition pd = new ProcessDefinition();
        pd.setId("processId");
        pd.setVersion("1.0");
        pd.setName("processName");
        pd.setRoles(singleton("admin"));
        pd.setAddons(singleton("process-management"));
        pd.setType("processType");

        MessageMarshaller.ProtoStreamWriter writer = mock(MessageMarshaller.ProtoStreamWriter.class);

        ProcessDefinitionMarshaller marshaller = new ProcessDefinitionMarshaller(null);
        marshaller.writeTo(writer, pd);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeString(ID, pd.getId());
        inOrder.verify(writer).writeString(VERSION, pd.getVersion());
        inOrder.verify(writer).writeString(NAME, pd.getName());
        inOrder.verify(writer).writeCollection(ROLES, pd.getRoles(), String.class);
        inOrder.verify(writer).writeCollection(ADDONS, pd.getAddons(), String.class);
        inOrder.verify(writer).writeString(TYPE, pd.getType());
    }
}
