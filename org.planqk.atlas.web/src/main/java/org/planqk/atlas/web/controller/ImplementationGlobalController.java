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

import java.util.UUID;

import org.hibernate.tool.schema.spi.DelayedDropAction;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.RevisionDto;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationRevisionAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.history.Revision;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller to access implementations outside of the context of its implemented algorithm.
 */
@Tag(name = Constants.TAG_IMPLEMENTATIONS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.IMPLEMENTATIONS)
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class ImplementationGlobalController {

    private final ImplementationService implementationService;

    private final ImplementationAssembler implementationAssembler;

    private final ImplementationRevisionAssembler implementationRevisionAssembler;

    private final ObjectMapper objectMapper;

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all implementations unaffected by its implemented algorithm")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementations(
        @Parameter(hidden = true) ListParameters listParameters) {
        final var implementations = implementationService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404",
            description = "Implementation with given ID doesn't exist")
    }, description = "Retrieve a specific implementation and its basic properties.")
    @GetMapping("/{implementationId}")
    public ResponseEntity<EntityModel<ImplementationDto>> getImplementation(
        @PathVariable UUID implementationId) {
        final var implementation = this.implementationService.findById(implementationId);
        return ResponseEntity.ok(implementationAssembler.toModel(implementation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    @ListParametersDoc
    @GetMapping("/{implementationId}/versions")
    public ResponseEntity<PagedModel<EntityModel<RevisionDto>>> getImplementationVersions(
            @PathVariable UUID implementationId, @Parameter(hidden = true) ListParameters listParameters) {
            implementationRevisionAssembler.setImplementationId(implementationId);
            return ResponseEntity.ok(implementationRevisionAssembler.toModel(implementationService.findImplementationVersions(implementationId, listParameters.getPageable())));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Implementation with given ID and revisionNumber doesn't exist")
    }, description = "Retrieve all versions of an implementation")
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.VERSIONS + "/{versionId}")
    public ResponseEntity<EntityModel<ImplementationDto>>getImplementationVersion(
            @PathVariable UUID implementationId, @PathVariable Integer versionId) {
        return ResponseEntity.ok(implementationAssembler.toModel(implementationService.findImplementationVersion(implementationId, versionId).getEntity()));
    }
}
