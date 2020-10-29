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

package org.planqk.atlas.web.controller;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.KnowledgeArtifact;
import org.planqk.atlas.core.services.DiscussionCommentService;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;
import org.planqk.atlas.web.linkassembler.DiscussionTopicAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Hidden
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_DISCUSSION_TOPIC)
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.DISCUSSION_TOPICS)
@ApiVersion("v1")
@Slf4j
@AllArgsConstructor
@RestController
public class DiscussionTopicController {

    private final DiscussionTopicService discussionTopicService;

    private final DiscussionTopicAssembler discussionTopicAssembler;

    private final DiscussionCommentService discussionCommentService;

    private final DiscussionCommentController discussionCommentController;

    @Operation(responses = {
        @ApiResponse(responseCode = "200")
    }, description = "")
    @ListParametersDoc
    @GetMapping
    public HttpEntity<PagedModel<EntityModel<DiscussionTopicDto>>> getDiscussionTopics(
        @Parameter(hidden = true) ListParameters listParameters) {
        final var topics = discussionTopicService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(discussionTopicAssembler.toModel(topics));
    }

    public HttpEntity<PagedModel<EntityModel<DiscussionTopicDto>>> getDiscussionTopics(
        UUID knowledgeArtifactId,
        @Parameter(hidden = true) ListParameters listParameters) {
        final var topics = discussionTopicService.findByKnowledgeArtifactId(knowledgeArtifactId, listParameters.getPageable());
        return ResponseEntity.ok(discussionTopicAssembler.toModel(topics));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{topicId}")
    public HttpEntity<EntityModel<DiscussionTopicDto>> getDiscussionTopic(@PathVariable UUID topicId) {
        final DiscussionTopic discussionTopic = discussionTopicService.findById(topicId);
        return ResponseEntity.ok(discussionTopicAssembler.toModel(discussionTopic));
    }

    public HttpEntity<EntityModel<DiscussionTopicDto>> getDiscussionTopic(UUID knowledgeArtifactId, UUID topicId) {
        discussionTopicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topicId, knowledgeArtifactId);
        final DiscussionTopic discussionTopic = discussionTopicService.findById(topicId);
        return ResponseEntity.ok(discussionTopicAssembler.toModel(discussionTopic));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @DeleteMapping("/{topicId}")
    public HttpEntity<Void> deleteDiscussionTopic(@PathVariable UUID topicId) {
        discussionTopicService.delete(topicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public HttpEntity<Void> deleteDiscussionTopic(UUID knowledgeArtifactId, UUID topicId) {
        discussionTopicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topicId, knowledgeArtifactId);
        discussionTopicService.delete(topicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @PostMapping()
    public HttpEntity<EntityModel<DiscussionTopicDto>> createDiscussionTopic(
        @Validated(ValidationGroups.Create.class) @RequestBody DiscussionTopicDto discussionTopicDto) {
        final var discussionTopic = discussionTopicService.create(ModelMapperUtils.convert(discussionTopicDto, DiscussionTopic.class));
        return new ResponseEntity<>(discussionTopicAssembler.toModel(discussionTopic), HttpStatus.CREATED);
    }

    public HttpEntity<EntityModel<DiscussionTopicDto>> createDiscussionTopic(
        KnowledgeArtifact knowledgeArtifact,
        @Validated(ValidationGroups.Create.class) @RequestBody DiscussionTopicDto discussionTopicDto) {
        final DiscussionTopic convertedDiscussionTopic = ModelMapperUtils.convert(discussionTopicDto, DiscussionTopic.class);
        convertedDiscussionTopic.setKnowledgeArtifact(knowledgeArtifact);
        final var discussionTopic = discussionTopicService.create(convertedDiscussionTopic);
        final Set<DiscussionTopic> discussionTopics = knowledgeArtifact.getDiscussionTopics();
        discussionTopics.add(discussionTopic);
        knowledgeArtifact.setDiscussionTopics(discussionTopics);
        return new ResponseEntity<>(discussionTopicAssembler.toModel(discussionTopic), HttpStatus.CREATED);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @ListParametersDoc
    @GetMapping("/{topicId}/" + Constants.DISCUSSION_COMMENTS)
    public HttpEntity<PagedModel<EntityModel<DiscussionCommentDto>>> getDiscussionComments(
        @PathVariable UUID topicId,
        @Parameter(hidden = true) ListParameters listParameters) {
        return discussionCommentController.getDiscussionCommentsOfTopic(topicId, listParameters);
    }

    public HttpEntity<PagedModel<EntityModel<DiscussionCommentDto>>> getDiscussionComments(
        UUID knowledgeArtifactId,
        UUID topicId,
        @Parameter(hidden = true) ListParameters listParameters) {
        discussionTopicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topicId, knowledgeArtifactId);
        return discussionCommentController.getDiscussionCommentsOfTopic(topicId, listParameters);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<EntityModel<DiscussionCommentDto>> getDiscussionComment(
        @PathVariable UUID topicId,
        @PathVariable UUID commentId) {
        discussionCommentService.checkIfDiscussionCommentIsInDiscussionTopic(commentId, topicId);
        return discussionCommentController.getDiscussionComment(commentId);
    }

    public HttpEntity<EntityModel<DiscussionCommentDto>> getDiscussionComment(
        UUID knowledgeArtifactId,
        UUID topicId,
        UUID commentId) {
        discussionTopicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topicId, knowledgeArtifactId);
        discussionCommentService.checkIfDiscussionCommentIsInDiscussionTopic(commentId, topicId);
        return discussionCommentController.getDiscussionComment(commentId);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @DeleteMapping("/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<Void> deleteDiscussionComment(@PathVariable UUID topicId, @PathVariable UUID commentId) {
        discussionCommentService.checkIfDiscussionCommentIsInDiscussionTopic(commentId, topicId);
        return discussionCommentController.deleteDiscussionComment(commentId);
    }

    public HttpEntity<Void> deleteDiscussionComment(UUID knowledgeArtifactId, UUID topicId, UUID commentId) {
        discussionTopicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topicId, knowledgeArtifactId);
        discussionCommentService.checkIfDiscussionCommentIsInDiscussionTopic(commentId, topicId);
        return discussionCommentController.deleteDiscussionComment(commentId);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @PutMapping("/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<EntityModel<DiscussionCommentDto>> updateDiscussionComment(
        @PathVariable UUID topicId,
        @PathVariable UUID commentId,
        @Validated(ValidationGroups.Update.class) @RequestBody DiscussionCommentDto discussionCommentDto) {
        discussionCommentService.checkIfDiscussionCommentIsInDiscussionTopic(commentId, topicId);
        discussionCommentDto.setId(commentId);
        return discussionCommentController.updateDiscussionComment(commentId, discussionCommentDto);
    }

    public HttpEntity<EntityModel<DiscussionCommentDto>> updateDiscussionComment(
        UUID knowledgeArtifactId,
        UUID topicId,
        UUID commentId,
        @Validated(ValidationGroups.Update.class) @RequestBody DiscussionCommentDto discussionCommentDto) {
        discussionTopicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topicId, knowledgeArtifactId);
        discussionCommentService.checkIfDiscussionCommentIsInDiscussionTopic(commentId, topicId);
        discussionCommentDto.setId(commentId);
        return discussionCommentController.updateDiscussionComment(commentId, discussionCommentDto);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @PostMapping("/{topicId}/" + Constants.DISCUSSION_COMMENTS)
    public HttpEntity<EntityModel<DiscussionCommentDto>> createDiscussionComment(
        @PathVariable UUID topicId,
        @Validated(ValidationGroups.Create.class) @RequestBody DiscussionCommentDto discussionCommentDto) {
        final DiscussionTopic discussionTopic = discussionTopicService.findById(topicId);
        discussionCommentDto.setDiscussionTopic(ModelMapperUtils.convert(discussionTopic, DiscussionTopicDto.class));
        return discussionCommentController.createDiscussionComment(discussionCommentDto);
    }

    public HttpEntity<EntityModel<DiscussionCommentDto>> createDiscussionComment(
        UUID knowledgeArtifactId,
        UUID topicId,
        @Validated(ValidationGroups.Create.class) @RequestBody DiscussionCommentDto discussionCommentDto) {
        discussionTopicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topicId, knowledgeArtifactId);
        final DiscussionTopic discussionTopic = discussionTopicService.findById(topicId);
        discussionCommentDto.setDiscussionTopic(ModelMapperUtils.convert(discussionTopic, DiscussionTopicDto.class));
        return discussionCommentController.createDiscussionComment(discussionCommentDto, discussionTopic.getKnowledgeArtifact());
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    }, description = "")
    @PutMapping("/{topicId}")
    public HttpEntity<EntityModel<DiscussionTopicDto>> updateDiscussionTopic(
        @PathVariable UUID topicId,
        @Validated(ValidationGroups.Update.class) @RequestBody DiscussionTopicDto discussionTopicDto) {
        discussionTopicDto.setId(topicId);
        final DiscussionTopic discussionTopic = discussionTopicService.update(ModelMapperUtils.convert(discussionTopicDto, DiscussionTopic.class));
        return ResponseEntity.ok(discussionTopicAssembler.toModel(discussionTopic));
    }

    public HttpEntity<EntityModel<DiscussionTopicDto>> updateDiscussionTopic(
        KnowledgeArtifact knowledgeArtifact,
        UUID topicId,
        @Validated(ValidationGroups.Update.class) @RequestBody DiscussionTopicDto discussionTopicDto) {
        discussionTopicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topicId, knowledgeArtifact.getId());
        discussionTopicDto.setId(topicId);
        final DiscussionTopic convertedDiscussionTopic = ModelMapperUtils.convert(discussionTopicDto, DiscussionTopic.class);
        convertedDiscussionTopic.setKnowledgeArtifact(knowledgeArtifact);
        final DiscussionTopic discussionTopic = discussionTopicService.update(convertedDiscussionTopic);
        return ResponseEntity.ok(discussionTopicAssembler.toModel(discussionTopic));
    }
}
