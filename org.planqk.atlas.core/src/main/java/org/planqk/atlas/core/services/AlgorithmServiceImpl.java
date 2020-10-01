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

package org.planqk.atlas.core.services;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Image;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ApplicationAreaRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyRepository;
import org.planqk.atlas.core.repository.PatternRelationRepository;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.planqk.atlas.core.repository.PublicationRepository;
import org.planqk.atlas.core.repository.ImageRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.repository.SketchRepository;
import org.planqk.atlas.core.util.ServiceUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {

    private final AlgorithmRepository algorithmRepository;

    private final SketchRepository sketchRepository;

    private final AlgorithmRelationRepository algorithmRelationRepository;

    private final ImplementationService implementationService;

    private final PublicationRepository publicationRepository;

    private final ProblemTypeRepository problemTypeRepository;

    private final ApplicationAreaRepository applicationAreaRepository;

    private final ComputeResourcePropertyRepository computeResourcePropertyRepository;

    private final PatternRelationService patternRelationService;
    private final PatternRelationRepository patternRelationRepository;

    private final ImplementationRepository implementationRepository;

    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public Algorithm create(Algorithm algorithm) {
        return algorithmRepository.save(algorithm);
    }

    @Override
    public Page<Algorithm> findAll(@NonNull Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return algorithmRepository.findAll(search, pageable);
        }
        return algorithmRepository.findAll(pageable);
    }

    @Override
    public Algorithm findById(@NonNull UUID algorithmId) {
        return ServiceUtils.findById(algorithmId, Algorithm.class, algorithmRepository);
    }

    @Override
    @Transactional
    public Algorithm update(@NonNull Algorithm algorithm) {
        Algorithm persistedAlgorithm = findById(algorithm.getId());

        persistedAlgorithm.setName(algorithm.getName());
        persistedAlgorithm.setAcronym(algorithm.getAcronym());
        persistedAlgorithm.setIntent(algorithm.getIntent());
        persistedAlgorithm.setProblem(algorithm.getProblem());
        persistedAlgorithm.setInputFormat(algorithm.getInputFormat());
        persistedAlgorithm.setAlgoParameter(algorithm.getAlgoParameter());
        persistedAlgorithm.setOutputFormat(algorithm.getOutputFormat());
        persistedAlgorithm.setSolution(algorithm.getSolution());
        persistedAlgorithm.setAssumptions(algorithm.getAssumptions());
        persistedAlgorithm.setComputationModel(algorithm.getComputationModel());

        if (algorithm instanceof QuantumAlgorithm) {
            QuantumAlgorithm quantumAlgorithm = (QuantumAlgorithm) algorithm;
            QuantumAlgorithm persistedQuantumAlgorithm = (QuantumAlgorithm) persistedAlgorithm;

            persistedQuantumAlgorithm.setNisqReady(quantumAlgorithm.isNisqReady());
            persistedQuantumAlgorithm.setQuantumComputationModel(quantumAlgorithm.getQuantumComputationModel());
            persistedQuantumAlgorithm.setSpeedUp(quantumAlgorithm.getSpeedUp());

            return algorithmRepository.save(persistedQuantumAlgorithm);
        } else {
            return algorithmRepository.save(persistedAlgorithm);
        }
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID algorithmId) {
        Algorithm algorithm = findById(algorithmId);

        removeReferences(algorithm);

        algorithmRepository.deleteById(algorithmId);
    }

    private void removeReferences(@NonNull Algorithm algorithm) {
        // delete related implementations
        algorithm.getImplementations().forEach(
                implementation -> implementationService.delete(implementation.getId()));

        // delete compute resource property
        algorithm.getRequiredComputeResourceProperties().forEach(computeResourcePropertyRepository::delete);

        // delete algorithm relations
        algorithm.getAlgorithmRelations().forEach(algorithmRelationRepository::delete);

        // delete related pattern relations
        algorithm.getRelatedPatterns().forEach(
                patternRelation -> patternRelationService.delete(patternRelation.getId()));

        // remove links to publications
        algorithm.getPublications().forEach(
                publication -> publication.removeAlgorithm(algorithm));

        // remove links to application areas
        algorithm.getApplicationAreas().forEach(
                applicationArea -> applicationArea.removeAlgorithm(algorithm));

        // remove links to problem types
        algorithm.getProblemTypes().forEach(
                problemType -> problemType.removeAlgorithm(algorithm));

        // remove link to tag
        algorithm.getTags().forEach(tag -> tag.removeAlgorithm(algorithm));
    }

    @Override
    public Page<AlgorithmRelation> findLinkedAlgorithmRelations(@NonNull UUID algorithmId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(algorithmId, Algorithm.class, algorithmRepository);

        return getAlgorithmRelations(algorithmId, pageable);
    }

    @Override
    public Page<PatternRelation> findLinkedPatternRelations(@NonNull UUID algorithmId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(algorithmId, Algorithm.class, algorithmRepository);

        return patternRelationRepository.findByAlgorithmId(algorithmId, pageable);
    }

    @Override
    public Page<Publication> findLinkedPublications(@NonNull UUID algorithmId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(algorithmId, Algorithm.class, algorithmRepository);

        return publicationRepository.findPublicationsByAlgorithmId(algorithmId, pageable);
    }

    @Override
    public Page<ProblemType> findLinkedProblemTypes(@NonNull UUID algorithmId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(algorithmId, Algorithm.class, algorithmRepository);

        return problemTypeRepository.findProblemTypesByAlgorithmId(algorithmId, pageable);
    }

    @Override
    public Page<ApplicationArea> findLinkedApplicationAreas(@NonNull UUID algorithmId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(algorithmId, Algorithm.class, algorithmRepository);

        return applicationAreaRepository.findApplicationAreasByAlgorithmId(algorithmId, pageable);
    }

    @Override
    public void checkIfPublicationIsLinkedToAlgorithm(UUID algorithmId, UUID publicationId) {
        Algorithm algorithm = findById(algorithmId);
        Publication publication = ServiceUtils.findById(publicationId, Publication.class, publicationRepository);

        if (!algorithm.getPublications().contains(publication)) {
            throw new NoSuchElementException("Publication with ID \"" + publicationId
                    + "\" is not linked to Algorithm with ID \"" + algorithmId +  "\"");
        }
    }

    @Override
    public void checkIfProblemTypeIsLinkedToAlgorithm(UUID algorithmId, UUID problemTypeId) {
        Algorithm algorithm = findById(algorithmId);
        ProblemType problemType = ServiceUtils.findById(problemTypeId, ProblemType.class, problemTypeRepository);

        if (!algorithm.getProblemTypes().contains(problemType)) {
            throw new NoSuchElementException("ProblemType with ID \"" + problemTypeId
                    + "\" is not linked to Algorithm with ID \"" + algorithmId +  "\"");
        }
    }

    @Override
    public void checkIfApplicationAreaIsLinkedToAlgorithm(UUID algorithmId, UUID applicationAreaId) {
        Algorithm algorithm = findById(algorithmId);
        ApplicationArea applicationArea = ServiceUtils.findById(applicationAreaId, ApplicationArea.class, applicationAreaRepository);

        if (!algorithm.getApplicationAreas().contains(applicationArea)) {
            throw new NoSuchElementException("ApplicationArea with ID \"" + applicationAreaId
                    + "\" is not linked to Algorithm with ID \"" + algorithmId +  "\"");
        }
    }

    private Page<AlgorithmRelation> getAlgorithmRelations(@NonNull UUID algorithmId, @NonNull Pageable pageable) {
        return algorithmRelationRepository.findBySourceAlgorithmIdOrTargetAlgorithmId(algorithmId, algorithmId, pageable);
    }
}
