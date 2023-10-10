package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.Comment;
import org.mockito.InOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.infinispan.protostream.CommentMarshaller.CONTENT;
import static org.kie.kogito.index.infinispan.protostream.CommentMarshaller.ID;
import static org.kie.kogito.index.infinispan.protostream.CommentMarshaller.UPDATED_AT;
import static org.kie.kogito.index.infinispan.protostream.CommentMarshaller.UPDATED_BY;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentMarshallerTest {

    String id = UUID.randomUUID().toString();
    String content = "CommentContent";
    String updatedBy = "CommentUpdatedBy";
    Date now = new Date();

    @Test
    void testReadFrom() throws IOException {
        MessageMarshaller.ProtoStreamReader reader = mock(MessageMarshaller.ProtoStreamReader.class);

        when(reader.readString(ID)).thenReturn(id);
        when(reader.readString(CONTENT)).thenReturn(content);
        when(reader.readString(UPDATED_BY)).thenReturn(updatedBy);
        when(reader.readDate(UPDATED_AT)).thenReturn(now);

        CommentMarshaller commentMarshaller = new CommentMarshaller(null);
        Comment comment = commentMarshaller.readFrom(reader);

        assertThat(comment)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID, id)
                .hasFieldOrPropertyWithValue(CONTENT, content)
                .hasFieldOrPropertyWithValue(UPDATED_BY, updatedBy)
                .hasFieldOrPropertyWithValue(UPDATED_AT, commentMarshaller.dateToZonedDateTime(now));

        InOrder inOrder = inOrder(reader);
        inOrder.verify(reader).readString(ID);
        inOrder.verify(reader).readString(CONTENT);
        inOrder.verify(reader).readString(UPDATED_BY);
        inOrder.verify(reader).readDate(UPDATED_AT);
    }

    @Test
    void testWriteTo() throws IOException {
        MessageMarshaller.ProtoStreamWriter writer = mock(MessageMarshaller.ProtoStreamWriter.class);
        CommentMarshaller marshaller = new CommentMarshaller(null);
        Comment comment = Comment.builder().id(id).content(content).updatedBy(updatedBy).updatedAt(marshaller.dateToZonedDateTime(now)).build();

        marshaller.writeTo(writer, comment);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeString(ID, id);
        inOrder.verify(writer).writeString(CONTENT, content);
        inOrder.verify(writer).writeString(UPDATED_BY, updatedBy);
        inOrder.verify(writer).writeDate(UPDATED_AT, now);
    }

    @Test
    void testMarshaller() {
        CommentMarshaller marshaller = new CommentMarshaller(null);
        assertThat(marshaller.getJavaClass()).isEqualTo(Comment.class);
        assertThat(marshaller.getTypeName()).isEqualTo(Comment.class.getName());
    }
}
