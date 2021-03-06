/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DiscussionCommentServiceTest extends AtlasDatabaseTestBase {

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @Autowired
    private DiscussionTopicService topicService;

    @Autowired
    private DiscussionCommentService commentService;

    @Autowired
    private PublicationService publicationService;

    private DiscussionComment comment;

    private DiscussionComment comment2;

    private DiscussionTopic topic;

    @BeforeEach
    public void initialize() {
        var pub = new Publication();
        pub.setTitle("discussion");
        pub = publicationService.create(pub);

        topic = new DiscussionTopic();
        topic.setKnowledgeArtifact(pub);

        comment = new DiscussionComment();
        comment.setDate(OffsetDateTime.now());
        comment.setText("Test Text");
        comment.setDiscussionTopic(topic);

        comment2 = new DiscussionComment();
        comment2.setDate(OffsetDateTime.now());
        comment2.setText("Test Text");
        comment2.setDiscussionTopic(topic);
    }

    @Test
    void createDiscussionComment() {
        topicService.create(this.topic);
        DiscussionComment comment = commentService.create(this.comment);
        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getDate()).isEqualTo(this.comment.getDate());
        assertThat(comment.getText()).isEqualTo(this.comment.getText());
        assertThat(comment.getDiscussionTopic()).isEqualTo(this.comment.getDiscussionTopic());
    }

    @Test
    void findAllDiscussionComments() {
        topicService.create(this.topic);
        commentService.create(this.comment);
        commentService.create(this.comment2);

        Page<DiscussionComment> discussionCommentPage = commentService.findAll(pageable);
        assertThat(discussionCommentPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void checkIfDiscussionCommentIsInDiscussionTopic_Linked() {
        var pub = new Publication();
        pub.setTitle("test");
        pub = publicationService.create(pub);

        var topic = new DiscussionTopic();
        topic.setKnowledgeArtifact(pub);
        topic.setTitle("test");
        var savedTopic = topicService.create(topic);

        var comment = new DiscussionComment();
        comment.setDiscussionTopic(savedTopic);
        comment.setText("test");
        var savedComment = commentService.create(comment);

        commentService.checkIfDiscussionCommentIsInDiscussionTopic(savedComment.getId(), savedTopic.getId());
    }

    @Test
    void checkIfDiscussionCommentIsInDiscussionTopic_Unlinked() {
        var pub = new Publication();
        pub.setTitle("test");
        pub = publicationService.create(pub);

        var topic = new DiscussionTopic();
        topic.setTitle("test");
        topic.setKnowledgeArtifact(pub);
        var savedTopic = topicService.create(topic);

        var secondTopic = new DiscussionTopic();
        secondTopic.setTitle("test2");
        secondTopic.setKnowledgeArtifact(pub);
        var savedSecondTopic = topicService.create(secondTopic);

        var comment = new DiscussionComment();
        comment.setDiscussionTopic(savedTopic);
        comment.setText("test");
        var savedComment = commentService.create(comment);

        assertThrows(NoSuchElementException.class, () -> {
            commentService.checkIfDiscussionCommentIsInDiscussionTopic(savedComment.getId(), savedSecondTopic.getId());
        });
    }

    // @Test
    void findDiscussionCommentById_ElementFound() {
        // TODO
    }

    @Test
    void findDiscussionCommentById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> commentService.findById(UUID.randomUUID()));
    }

    @Test
    void updateDiscussionComment_ElementFound() {
        topicService.create(this.topic);
        DiscussionComment comment = commentService.create(this.comment);
        comment.setText("New Text");
        DiscussionComment update = commentService.update(comment);

        assertThat(update.getDate()).isEqualTo(comment.getDate());
        assertThat(update.getText()).isEqualTo(comment.getText());
        assertThat(update.getDiscussionTopic()).isEqualTo(comment.getDiscussionTopic());
    }

    @Test
    void updateDiscussionComment_ElementNotFound() {
        comment.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> commentService.update(comment));
    }

    @Test
    void deleteDiscussionComment_ElementFound() {
        topicService.create(this.topic);
        DiscussionComment comment = commentService.create(this.comment);
        commentService.delete(comment.getId());
        assertThrows(NoSuchElementException.class, () -> {
            commentService.findById(comment.getId());
        });
    }

    // @Test
    void deleteDiscussionComment_ElementNotFound() {
        // TODO
    }
}
