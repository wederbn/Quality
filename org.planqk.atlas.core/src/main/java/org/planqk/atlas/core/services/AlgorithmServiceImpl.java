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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {

    private final static Logger LOG = LoggerFactory.getLogger(AlgorithmServiceImpl.class);

    private final AlgorithmRepository algorithmRepository;
    private final AlgorithmRelationRepository algorithmRelationRepository;
    private final PatternRelationService patternRelationService;
    private final AlgoRelationTypeService relationTypeService;
    private final ImplementationRepository implementationRepository;

    @Transactional
    @Override
    public Algorithm save(Algorithm algorithm) {
        return algorithmRepository.save(algorithm);
    }

    private Set<AlgorithmRelation> getValidAlgorithmRelations(Algorithm algorithm, Set<AlgorithmRelation> inputRelations) {
        // save relations to append after persisting algorithm

        Set<AlgorithmRelation> validRelations = new HashSet<>();

        for (AlgorithmRelation relation : inputRelations) {
            // set correct source algorithm
            relation.setSourceAlgorithm(algorithm);
            if (algorithmRepository.existsAlgorithmById((relation.getTargetAlgorithm().getId()))) {
                relation.setAlgoRelationType(getPersistedAlgoRelationType(relation));
                validRelations.add(relation);
            }
        }

        return validRelations;
    }

    private AlgoRelationType getPersistedAlgoRelationType(AlgorithmRelation relation) {
        // TODO decide if missing AlgoRelationType causes exception or if it gets created
        // AlgoRelationType gets created on the fly if it does not exist yet
        return relationTypeService
                .findOptionalById(relation.getAlgoRelationType().getId()).isPresent()
                ? relation.getAlgoRelationType()
                : relationTypeService.save(relation.getAlgoRelationType());
    }

    @Transactional
    @Override
    public Algorithm update(UUID id, Algorithm algorithm) {
        LOG.info("Trying to update algorithm");
        Algorithm persistedAlg = algorithmRepository.findById(id).orElseThrow(NoSuchElementException::new);

        persistedAlg.setName(algorithm.getName());
        persistedAlg.setAcronym(algorithm.getAcronym());
        // persistedAlg.setPublications(algorithm.getPublications());
        persistedAlg.setIntent(algorithm.getIntent());
        persistedAlg.setProblem(algorithm.getProblem());
        // persistedAlg.setAlgorithmRelations(algorithm.getAlgorithmRelations());
        persistedAlg.setInputFormat(algorithm.getInputFormat());
        persistedAlg.setAlgoParameter(algorithm.getAlgoParameter());
        persistedAlg.setOutputFormat(algorithm.getOutputFormat());
        persistedAlg.setSketch(algorithm.getSketch());
        persistedAlg.setSolution(algorithm.getSolution());
        persistedAlg.setAssumptions(algorithm.getAssumptions());
        persistedAlg.setComputationModel(algorithm.getComputationModel());
        // persistedAlg.setRelatedPatterns(algorithm.getRelatedPatterns());
        persistedAlg.setProblemTypes(algorithm.getProblemTypes());
        persistedAlg.setApplicationAreas(algorithm.getApplicationAreas());
        persistedAlg.setTags(algorithm.getTags());
        persistedAlg.setRequiredComputingResourceProperties(persistedAlg.getRequiredComputingResourceProperties());

        // If QuantumAlgorithm adjust Quantum fields
        if (algorithm instanceof QuantumAlgorithm) {
            QuantumAlgorithm quantumAlgorithm = (QuantumAlgorithm) algorithm;
            QuantumAlgorithm persistedQuantumAlg = (QuantumAlgorithm) persistedAlg;

            persistedQuantumAlg.setNisqReady(quantumAlgorithm.isNisqReady());
            persistedQuantumAlg.setQuantumComputationModel(quantumAlgorithm.getQuantumComputationModel());
            persistedQuantumAlg.setSpeedUp(quantumAlgorithm.getSpeedUp());

            return algorithmRepository.save(persistedQuantumAlg);
        } else {
            // Else if ClassicAlgorithm no more fields to adjust
            return algorithmRepository.save(persistedAlg);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Optional<Algorithm> algorithmOptional = algorithmRepository.findById(id);
        if (algorithmOptional.isEmpty()) {
            return;
        }
        Algorithm algorithm = algorithmOptional.get();

        // delete related implementations
        implementationRepository.findByImplementedAlgorithm(algorithm).forEach(implementationRepository::delete);

        // delete ingoing and outgoing algorithm relations
        Set<AlgorithmRelation> linkedAsTargetRelations = algorithmRelationRepository.findByTargetAlgorithmId(id);
        linkedAsTargetRelations.addAll(algorithmRelationRepository.findBySourceAlgorithmId(id));
        for (AlgorithmRelation relation : linkedAsTargetRelations) {
            algorithmRelationRepository.delete(relation);
        }

        // delete related pattern relations
        patternRelationService.findByAlgorithmId(id).forEach(patternRelation -> patternRelationService.deleteById(patternRelation.getId()));

        //Remove all publications associations
        algorithmRepository.deleteAssociationsOfAlgorithm(id);

        algorithmRepository.deleteById(id);
    }

    @Override
    public Page<Algorithm> findAll(Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return algorithmRepository.findAll(search, pageable);
        }
        return algorithmRepository.findAll(pageable);
    }

    @Override
    public Algorithm findById(UUID algoId) {
        return findOptionalById(algoId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<Algorithm> findOptionalById(UUID algoId) {
        return algorithmRepository.findById(algoId);
    }

    @Transactional
    @Override
    public AlgorithmRelation addOrUpdateAlgorithmRelation(UUID sourceAlgorithmId, AlgorithmRelation relation) {
        // Read involved Algorithms from database
        Algorithm sourceAlgorithm = findById(sourceAlgorithmId);
        Algorithm targetAlgorithm = findById(relation.getTargetAlgorithm().getId());

        if (relation.getAlgoRelationType().getId() == null) {
            relationTypeService.save(relation.getAlgoRelationType());
        }
        Optional<AlgoRelationType> relationTypeOpt = relationTypeService
                .findOptionalById(relation.getAlgoRelationType().getId());

        // Create relation type if not exists
        AlgoRelationType relationType = relationTypeOpt.isPresent()
                ? relationTypeOpt.get()
                : relationTypeService.save(relation.getAlgoRelationType());

        // Check if relation with those two algorithms and the relation type already
        // exists
        Optional<AlgorithmRelation> persistedRelationOpt = algorithmRelationRepository
                .findBySourceAlgorithmIdAndTargetAlgorithmIdAndAlgoRelationTypeId(sourceAlgorithm.getId(),
                        targetAlgorithm.getId(), relationType.getId());

        // If relation between the two algorithms already exists, update it
        if (persistedRelationOpt.isPresent()) {
            AlgorithmRelation persistedRelation = persistedRelationOpt.get();
            persistedRelation.setDescription(relation.getDescription());
            // Return updated relation
            return algorithmRelationRepository.save(persistedRelation);
        }

        // Set Relation Objects with referenced database objects
        relation.setSourceAlgorithm(sourceAlgorithm);
        relation.setTargetAlgorithm(targetAlgorithm);
        relation.setAlgoRelationType(relationType);

        sourceAlgorithm.addAlgorithmRelation(relation);

        // Save updated Algorithm -> CASCADE will save Relation
        this.algorithmRepository.save(sourceAlgorithm);
        persistedRelationOpt = algorithmRelationRepository
                .findBySourceAlgorithmIdAndTargetAlgorithmIdAndAlgoRelationTypeId(sourceAlgorithm.getId(),
                        targetAlgorithm.getId(), relationType.getId());

        return persistedRelationOpt.get();
    }

    @Override
    public void deleteAlgorithmRelation(UUID algoId, UUID relationId) {
        // Get involved Objects from database
        Algorithm sourceAlgorithm = algorithmRepository.findById(algoId)
                .orElseThrow(() -> new NoSuchElementException("Algorithm does not exist!"));
        AlgorithmRelation relation = algorithmRelationRepository.findById(relationId)
                .orElseThrow(() -> new NoSuchElementException("Relation does not exist!"));

        Set<AlgorithmRelation> algorithmRelations = sourceAlgorithm.getAlgorithmRelations();
        algorithmRelations.remove(relation);
        algorithmRepository.save(sourceAlgorithm);
    }

    @Override
    public Set<AlgorithmRelation> getAlgorithmRelations(UUID sourceAlgorithmId) {
        Set<AlgorithmRelation> relations = algorithmRelationRepository.findBySourceAlgorithmId(sourceAlgorithmId);
        relations.addAll(algorithmRelationRepository.findByTargetAlgorithmId(sourceAlgorithmId));
        return relations;
    }
}
