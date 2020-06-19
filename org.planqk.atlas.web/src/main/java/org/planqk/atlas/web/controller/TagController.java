/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
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

//@io.swagger.v3.oas.annotations.tags.Tag(name = "tag")
//@RestController
//@CrossOrigin(allowedHeaders = "*", origins = "*")
//@RequestMapping("/" + Constants.TAGS)
//@AllArgsConstructor
public class TagController {

//    private TagService tagService;
//    private PagedResourcesAssembler<TagDto> paginationAssembler;
//    //    private TagAssembler tagAssembler;
//    private AlgorithmAssembler algorithmAssembler;
//    private ImplementationAssembler implementationAssembler;

//    @Operation(responses = { @ApiResponse(responseCode = "200") })
//    @GetMapping(value = "/")
//    public HttpEntity<PagedModel<EntityModel<TagDto>>> getTags(@RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer size) {
//        // Generate Pageable
//        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
//        // Retrieve Page of DTOs
//        Page<TagDto> tags = ModelMapperUtils.convertPage(tagService.findAll(p), TagDto.class);
//        // Generate PagedModel
//        PagedModel<EntityModel<TagDto>> outputDto = paginationAssembler.toModel(tags);
//        tagAssembler.addLinks(outputDto.getContent());
//        return new ResponseEntity<>(outputDto, HttpStatus.OK);
//    }
//
//    @Operation(responses = { @ApiResponse(responseCode = "201") })
//    @PostMapping(value = "/")
//    public HttpEntity<EntityModel<TagDto>> createTag(@Valid @RequestBody TagDto tag) {
//        // Persist new tag
//        Tag savedTag = tagService.save(ModelMapperUtils.convert(tag, Tag.class));
//        // Convert to EntityModel-DTO
//        EntityModel<TagDto> dtoOutput = HateoasUtils
//                .generateEntityModel(ModelMapperUtils.convert(savedTag, TagDto.class));
//        // Add Links
//        tagAssembler.addLinks(dtoOutput);
//        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
//    }
//
//    @Operation(responses = { @ApiResponse(responseCode = "200") })
//    @GetMapping(value = "/{tagId}")
//    public HttpEntity<EntityModel<TagDto>> getTagById(@PathVariable UUID tagId) {
//        // Get Tag
//        Tag tag = tagService.getTagById(tagId);
//        // Get EntityModel of Tag-Object
//        EntityModel<TagDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(tag, TagDto.class));
//        // Add links
//        tagAssembler.addLinks(dtoOutput);
//        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
//    }
//
//    @Operation(responses = { @ApiResponse(responseCode = "200") })
//    @GetMapping(value = "/{tagId}/" + Constants.ALGORITHMS)
//    public HttpEntity<CollectionModel<EntityModel<AlgorithmDto>>> getAlgorithmsOfTag(@PathVariable UUID tagId) {
//        // Get Tag
//        Tag tag = tagService.getTagById(tagId);
//        // Retrieve Algorithms of Tag
//        Set<Algorithm> algorithms = tag.getAlgorithms();
//        // Translate Entity to DTO
//        Set<AlgorithmDto> algorithmDtos = ModelMapperUtils.convertSet(algorithms, AlgorithmDto.class);
//        // Create CollectionModel
//        CollectionModel<EntityModel<AlgorithmDto>> resultCollection = HateoasUtils
//                .generateCollectionModel(algorithmDtos);
//        // Fill EntityModels
//        algorithmAssembler.addLinks(resultCollection.getContent());
//        // Fill CollectionModel
//        tagAssembler.addAlgorithmLink(resultCollection, tagId);
//        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
//    }
//
//    @Operation(responses = { @ApiResponse(responseCode = "200") })
//    @GetMapping(value = "/{tagId}/" + Constants.IMPLEMENTATIONS)
//    public HttpEntity<CollectionModel<EntityModel<ImplementationDto>>> getImplementationsOfTag(
//            @PathVariable UUID tagId) {
//        // Get Tag
//        Tag tag = this.tagService.getTagById(tagId);
//        // Get ImplementationDTOs of Tag
//        Set<ImplementationDto> implementations = ModelMapperUtils.convertSet(tag.getImplementations(),
//                ImplementationDto.class);
//        // Create CollectionModel
//        CollectionModel<EntityModel<ImplementationDto>> resultCollection = HateoasUtils
//                .generateCollectionModel(implementations);
//        // Fill EntityModels
//        implementationAssembler.addLinks(resultCollection.getContent());
//        // Fill CollectionModel
//        tagAssembler.addImplementationLink(resultCollection, tagId);
//        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
//    }
}
