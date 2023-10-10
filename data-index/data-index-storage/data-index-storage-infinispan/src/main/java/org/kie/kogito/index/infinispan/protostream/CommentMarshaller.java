package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommentMarshaller extends AbstractMarshaller implements MessageMarshaller<Comment> {
    protected static final String ID = "id";
    protected static final String CONTENT = "content";
    protected static final String UPDATED_BY = "updatedBy";
    protected static final String UPDATED_AT = "updatedAt";

    public CommentMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public Comment readFrom(ProtoStreamReader reader) throws IOException {
        Comment comment = new Comment();
        comment.setId(reader.readString(ID));
        comment.setContent(reader.readString(CONTENT));
        comment.setUpdatedBy(reader.readString(UPDATED_BY));
        comment.setUpdatedAt(dateToZonedDateTime(reader.readDate(UPDATED_AT)));
        return comment;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Comment comment) throws IOException {
        writer.writeString("id", comment.getId());
        writer.writeString("content", comment.getContent());
        writer.writeString("updatedBy", comment.getUpdatedBy());
        writer.writeDate("updatedAt", zonedDateTimeToDate(comment.getUpdatedAt()));
    }

    @Override
    public Class<? extends Comment> getJavaClass() {
        return Comment.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
