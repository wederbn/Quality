/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.services.ComputeResourcePropertyTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = Constants.TAG_COMPUTE_RESOURCE_PROPERTY_TYPES)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.COMPUTE_RESOURCE_PROPERTY_TYPES)
@AllArgsConstructor
@Slf4j
public class ComputeResourcePropertyTypeController {

    private final ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all compute resource property types.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<ComputeResourcePropertyTypeDto>> getResourcePropertyTypes(
            @Parameter(hidden = true) ListParameters listParameters) {
        final var savedComputeResourcePropertyType = computeResourcePropertyTypeService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(savedComputeResourcePropertyType, ComputeResourcePropertyTypeDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Define the basic properties of an compute resource property type.")
    @PostMapping
    public ResponseEntity<ComputeResourcePropertyTypeDto> createComputingResourcePropertyType(
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyTypeDto computeResourcePropertyTypeDto) {
        final var savedComputeResourcePropertyType = computeResourcePropertyTypeService.create(
                ModelMapperUtils.convert(computeResourcePropertyTypeDto, ComputeResourcePropertyType.class));
        return new ResponseEntity<>(ModelMapperUtils.convert(savedComputeResourcePropertyType, ComputeResourcePropertyTypeDto.class),
                HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Compute resource property type with given ID doesn't exist")
    }, description = "Update the basic properties of an compute resource property type (e.g. name).")
    @PutMapping("/{computeResourcePropertyTypeId}")
    public ResponseEntity<ComputeResourcePropertyTypeDto> updateComputingResourcePropertyType(
            @PathVariable UUID computeResourcePropertyTypeId,
            @Validated(ValidationGroups.Update.class) @RequestBody
                    ComputeResourcePropertyTypeDto computeResourcePropertyTypeDto) {
        computeResourcePropertyTypeDto.setId(computeResourcePropertyTypeId);
        final var savedComputeResourcePropertyType = computeResourcePropertyTypeService.update(
                ModelMapperUtils.convert(computeResourcePropertyTypeDto, ComputeResourcePropertyType.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(savedComputeResourcePropertyType, ComputeResourcePropertyTypeDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                         description = "Bad Request. Compute resource property type is still in use by at least one compute resource property"),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Compute resource property type with given ID doesn't exist")
    }, description = "Delete an compute resource property type.")
    @DeleteMapping("/{computeResourcePropertyTypeId}")
    public ResponseEntity<Void> deleteComputingResourcePropertyType(
            @PathVariable UUID computeResourcePropertyTypeId) {
        computeResourcePropertyTypeService.delete(computeResourcePropertyTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Compute resource property type with given ID doesn't exist")
    }, description = "Retrieve a specific compute resource property type and its basic properties.")
    @GetMapping("/{computeResourcePropertyTypeId}")
    public ResponseEntity<ComputeResourcePropertyTypeDto> getComputingResourcePropertyType(
            @PathVariable UUID computeResourcePropertyTypeId) {
        final var computeResourcePropertyType = computeResourcePropertyTypeService.findById(computeResourcePropertyTypeId);
        return ResponseEntity.ok(ModelMapperUtils.convert(computeResourcePropertyType, ComputeResourcePropertyTypeDto.class));
    }
}
