package org.kie.kogito.index.postgresql.mapper;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.postgresql.model.AttachmentEntity;
import org.kie.kogito.index.postgresql.model.CommentEntity;
import org.kie.kogito.index.postgresql.model.UserTaskInstanceEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.junit.QuarkusTest;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class UserTaskInstanceEntityMapperIT {

    UserTaskInstance userTaskInstance = new UserTaskInstance();
    UserTaskInstanceEntity userTaskInstanceEntity = new UserTaskInstanceEntity();

    @Inject
    ObjectMapper jsonMapper;

    @Inject
    UserTaskInstanceEntityMapper mapper;

    @BeforeEach
    void setup() {
        String testId = "testId";
        String description = "testDescription";
        String name = "testName";
        String priority = "10";
        String processInstanceId = "testProcessInstanceId";
        String state = "testState";
        String actualOwner = "testActualOwner";
        Set<String> adminGroups = singleton("testAdminGroups");
        Set<String> adminUsers = singleton("testAdminUsers");
        ZonedDateTime time = ZonedDateTime.now();
        Set<String> excludedUsers = singleton("testExcludedUsers");
        Set<String> potentialGroups = singleton("testPotentialGroups");
        Set<String> potentialUsers = singleton("testPotentialUsers");
        String referenceName = "testReferenceName";
        String processId = "testProcessId";
        String rootProcessId = "testRootProcessId";
        String rootProcessInstanceId = "testRootProcessInstanceId";
        Map<String, String> object = new HashMap<>();
        object.put("test", "testValue");
        ObjectNode inputs = jsonMapper.createObjectNode().put("testInput", "testValue");
        ObjectNode outputs = jsonMapper.createObjectNode().put("testOutput", "testValue");

        String commentId = "testCommentId";
        String comment_content = "testCommentContent";
        String comment_updatedBy = "testCommentUpdatedBy";
        Comment comment = Comment.builder().id(commentId).updatedAt(time).updatedBy(comment_updatedBy).content(comment_content).build();
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setId(commentId);
        commentEntity.setContent(comment_content);
        commentEntity.setUpdatedAt(time);
        commentEntity.setUpdatedBy(comment_updatedBy);
        commentEntity.setUserTask(userTaskInstanceEntity);

        String attachmentId = "testAttachmentId";
        String attachment_name = "testAttachmentName";
        String attachment_content = "testAttachmentContent";
        String attachment_updatedBy = "testAttachmentUpdatedBy";
        Attachment attachment = Attachment.builder().id(attachmentId).updatedAt(time).updatedBy(attachment_updatedBy)
                .content(attachment_content).name(attachment_name).build();
        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setId(attachmentId);
        attachmentEntity.setContent(attachment_content);
        attachmentEntity.setName(attachment_name);
        attachmentEntity.setUpdatedAt(time);
        attachmentEntity.setUpdatedBy(attachment_updatedBy);
        attachmentEntity.setUserTask(userTaskInstanceEntity);

        userTaskInstance.setId(testId);
        userTaskInstance.setDescription(description);
        userTaskInstance.setName(name);
        userTaskInstance.setPriority(priority);
        userTaskInstance.setProcessInstanceId(processInstanceId);
        userTaskInstance.setState(state);
        userTaskInstance.setActualOwner(actualOwner);
        userTaskInstance.setAdminGroups(adminGroups);
        userTaskInstance.setAdminUsers(adminUsers);
        userTaskInstance.setCompleted(time);
        userTaskInstance.setStarted(time);
        userTaskInstance.setExcludedUsers(excludedUsers);
        userTaskInstance.setPotentialGroups(potentialGroups);
        userTaskInstance.setPotentialUsers(potentialUsers);
        userTaskInstance.setReferenceName(referenceName);
        userTaskInstance.setLastUpdate(time);
        userTaskInstance.setProcessId(processId);
        userTaskInstance.setRootProcessId(rootProcessId);
        userTaskInstance.setRootProcessInstanceId(rootProcessInstanceId);
        userTaskInstance.setInputs(inputs);
        userTaskInstance.setOutputs(outputs);
        userTaskInstance.setComments(singletonList(comment));
        userTaskInstance.setAttachments(singletonList(attachment));

        userTaskInstanceEntity.setId(testId);
        userTaskInstanceEntity.setDescription(description);
        userTaskInstanceEntity.setName(name);
        userTaskInstanceEntity.setPriority(priority);
        userTaskInstanceEntity.setProcessInstanceId(processInstanceId);
        userTaskInstanceEntity.setState(state);
        userTaskInstanceEntity.setActualOwner(actualOwner);
        userTaskInstanceEntity.setAdminGroups(adminGroups);
        userTaskInstanceEntity.setAdminUsers(adminUsers);
        userTaskInstanceEntity.setCompleted(time);
        userTaskInstanceEntity.setStarted(time);
        userTaskInstanceEntity.setExcludedUsers(excludedUsers);
        userTaskInstanceEntity.setPotentialGroups(potentialGroups);
        userTaskInstanceEntity.setPotentialUsers(potentialUsers);
        userTaskInstanceEntity.setReferenceName(referenceName);
        userTaskInstanceEntity.setLastUpdate(time);
        userTaskInstanceEntity.setProcessId(processId);
        userTaskInstanceEntity.setRootProcessId(rootProcessId);
        userTaskInstanceEntity.setRootProcessInstanceId(rootProcessInstanceId);
        userTaskInstanceEntity.setInputs(inputs);
        userTaskInstanceEntity.setOutputs(outputs);
        userTaskInstanceEntity.setComments(singletonList(commentEntity));
        userTaskInstanceEntity.setAttachments(singletonList(attachmentEntity));
    }

    @Test
    void testMapToEntity() {
        UserTaskInstanceEntity result = mapper.mapToEntity(userTaskInstance);
        assertThat(result).isEqualToIgnoringGivenFields(userTaskInstanceEntity, "$$_hibernate_tracker");
    }

    @Test
    void testMapToModel() {
        UserTaskInstance result = mapper.mapToModel(userTaskInstanceEntity);
        assertThat(result).isEqualToComparingFieldByField(userTaskInstance);
    }
}
