package org.planqk.atlas.web.controller;

import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.model.exceptions.SqlConsistencyException;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.planqk.atlas.web.linkassembler.AlgoRelationTypeAssembler;
import org.planqk.atlas.web.utils.DtoEntityConverter;
import org.planqk.atlas.web.utils.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGO_RELATION_TYPES)
public class AlgoRelationTypeController {
	
	@Autowired
	private AlgoRelationTypeService algoRelationTypeService;
	@Autowired
    private DtoEntityConverter modelConverter;
	@Autowired
	private PagedResourcesAssembler<AlgoRelationTypeDto> paginationAssembler;
	@Autowired
	private AlgoRelationTypeAssembler algoRelationTypeAssembler;

//	public static AlgoRelationTypeListDto createAlgoRelationTypeDtoList(Stream<AlgoRelationType> stream) {
//		AlgoRelationTypeListDto algoRelationTypeListDto = new AlgoRelationTypeListDto();
//		algoRelationTypeListDto.add(stream.map(algoRelationType -> createAlgoRelationTypeDto(algoRelationType)).collect(Collectors.toList()));
//		return algoRelationTypeListDto;
//	}
//
//	public static AlgoRelationTypeDto createAlgoRelationTypeDto(AlgoRelationType algoRelationType) {
//		AlgoRelationTypeDto dto = AlgoRelationTypeDto.Converter.convert(algoRelationType);
//		dto.add(linkTo(methodOn(AlgoRelationTypeController.class).getAlgoRelationTypeById(algoRelationType.getId())).withSelfRel());
//		return dto;
//	}

	@PostMapping("/")
	public HttpEntity<EntityModel<AlgoRelationTypeDto>> createAlgoRelationType(@Validated @RequestBody AlgoRelationTypeDto algoRelationTypeDto) {
		// Convert DTO to entity
		AlgoRelationType entityInput = modelConverter.convert(algoRelationTypeDto, AlgoRelationType.class);
		// save entity
		AlgoRelationType savedAlgoRelationType = algoRelationTypeService.save(entityInput);
		// convert entity to DTO
		AlgoRelationTypeDto dtoOutput = modelConverter.convert(savedAlgoRelationType, AlgoRelationTypeDto.class);
		return new ResponseEntity<>(algoRelationTypeAssembler.generateEntityModel(dtoOutput), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public HttpEntity<EntityModel<AlgoRelationTypeDto>> updateAlgoRelationType(@PathVariable UUID id,
			@Validated @RequestBody AlgoRelationTypeDto algoRelationTypeDto) throws NotFoundException {
		// Convert DTO to entity
		AlgoRelationType entityInput = modelConverter.convert(algoRelationTypeDto, AlgoRelationType.class);
		// update entity
		AlgoRelationType savedAlgoRelationType = algoRelationTypeService.update(id, entityInput);
		// convert entity to DTO
		AlgoRelationTypeDto dtoOutput = modelConverter.convert(savedAlgoRelationType, AlgoRelationTypeDto.class);
		return new ResponseEntity<>(algoRelationTypeAssembler.generateEntityModel(dtoOutput), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public HttpEntity<AlgoRelationTypeDto> deleteAlgoRelationType(@PathVariable UUID id) throws SqlConsistencyException, NotFoundException {
		// delete entity by id
		algoRelationTypeService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/")
	public HttpEntity<?> getAlgoRelationTypes(@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		// Generate pageable
		Pageable p = RestUtils.getPageableFromRequestParams(page, size);
		// Get entities
		Page<AlgoRelationType> entities = algoRelationTypeService.findAll(p);
		// convert to DTO pageable
		Page<AlgoRelationTypeDto> dtos = modelConverter.convertPage(entities, AlgoRelationTypeDto.class);
		// generate paged model with links
		PagedModel<EntityModel<AlgoRelationTypeDto>> pagedEntityOutput = paginationAssembler.toModel(dtos);
		// add links
		algoRelationTypeAssembler.addLinks(pagedEntityOutput.getContent());
		return new ResponseEntity<>(pagedEntityOutput, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public HttpEntity<EntityModel<AlgoRelationTypeDto>> getAlgoRelationTypeById(@PathVariable UUID id) throws NotFoundException {
		AlgoRelationType algoRelationType = algoRelationTypeService.findById(id);
		// convert entity to DTO
		AlgoRelationTypeDto dtoOutput = modelConverter.convert(algoRelationType, AlgoRelationTypeDto.class);
		return new ResponseEntity<>(algoRelationTypeAssembler.generateEntityModel(dtoOutput), HttpStatus.OK);
	}
}