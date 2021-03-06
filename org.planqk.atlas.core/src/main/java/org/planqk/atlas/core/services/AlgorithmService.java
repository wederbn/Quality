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

import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.LearningMethod;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for operations related to interacting and modifying {@link Algorithm}s in the database.
 */
public interface AlgorithmService {

    /**
     * Creates a new database entry for a given {@link Algorithm} and save it to the database.
     * <p>
     * The ID of the {@link Algorithm} parameter should be null, since the ID will be generated by the database when
     * creating the entry. The validation for this is done by the Controller layer, which will reject {@link Algorithm}s
     * with a given ID in its create path.
     *
     * @param algorithm The {@link Algorithm} object describing the properties of an algorithm that should be saved to
     *                  the database
     * @return The {@link Algorithm} object that represents the saved status from the database
     */
    @Transactional
    Algorithm create(Algorithm algorithm);

    /**
     * Retrieve multiple {@link Algorithm} entries from the database.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a
     * {@link Page} with all entries is queried.
     * <p>
     * If no search should be executed the search parameter can be left null or empty.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @param search   The string based on which a search will be executed
     * @return The page of queried {@link Algorithm} entries
     */
    Page<Algorithm> findAll(Pageable pageable, String search);

    /**
     * Find a database entry of a {@link Algorithm} that is already saved in the database. This search is based on the
     * ID the database has given the {@link Algorithm} object when it was created and first saved to the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want to find
     * @return The {@link Algorithm} with the given ID
     */
    Algorithm findById(UUID algorithmId);

    /**
     * Update an existing {@link Algorithm} database entry by saving the updated {@link Algorithm} object to the the
     * database.
     * <p>
     * The ID of the {@link Algorithm} parameter has to be set to the ID of the database entry we want to update. The
     * validation for this ID to be set is done by the Controller layer, which will reject {@link Algorithm}s without a
     * given ID in its update path. This ID will be used to query the existing {@link Algorithm} entry we want to
     * update. If no {@link Algorithm} entry with the given ID is found this method will throw a {@link
     * java.util.NoSuchElementException}.
     *
     * @param algorithm The {@link Algorithm} we want to update with its updated properties
     * @return the updated {@link Algorithm} object that represents the updated status of the database
     */
    @Transactional
    Algorithm update(Algorithm algorithm);

    /**
     * Delete an existing {@link Algorithm} entry from the database. This deletion is based on the ID the database has
     * given the {@link Algorithm} when it was created and first saved to the database.
     * <p>
     * When deleting an {@link Algorithm} multiple objects referenced by the {@link Algorithm}  will be deleted together
     * with it. All {@link org.planqk.atlas.core.model.Implementation}s, {@link org.planqk.atlas.core.model.ComputeResourceProperty}s,
     * {@link AlgorithmRelation}s, {@link PatternRelation}s and {@link org.planqk.atlas.core.model.Sketch}es that are
     * related to the {@link Algorithm}  will be deleted as well, since they can only be related to one {@link
     * Algorithm} at a time.
     * <p>
     * Objects that can be related to multiple {@link Algorithm}s will not be deleted. Only the reference to the deleted
     * {@link Algorithm} will be removed from these objects. These include {@link Publication}s, {@link ProblemType}s,
     * {@link ApplicationArea}s and {@link org.planqk.atlas.core.model.Tag}s.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want to delete
     */
    @Transactional
    void delete(UUID algorithmId);

    /**
     * Retrieve multiple {@link AlgorithmRelation}s entries from the database where the given {@link Algorithm} is
     * either source or target algorithm. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a
     * {@link Page} with all entries is queried.
     * <p>
     * The given {@link Algorithm} is identified through its ID given as a parameter. If no {@link Algorithm} with the
     * given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want find linked {@link AlgorithmRelation}s for
     * @param pageable    The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link AlgorithmRelation} entries which are linked to the {@link Algorithm}
     */
    Page<AlgorithmRelation> findLinkedAlgorithmRelations(UUID algorithmId, Pageable pageable);

    /**
     * Retrieve multiple {@link PatternRelation}s entries from the database where the pattern is related to the given
     * {@link Algorithm}. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a
     * {@link Page} with all entries is queried.
     * <p>
     * The given {@link Algorithm} is identified through its ID given as a parameter. If no {@link Algorithm} with the
     * given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want find linked {@link PatternRelation}s for
     * @param pageable    The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link PatternRelation} entries which are linked to the {@link Algorithm}
     */
    Page<PatternRelation> findLinkedPatternRelations(UUID algorithmId, Pageable pageable);

    /**
     * Retrieve multiple {@link Publication}s entries from the database of {@link Publication}s that are linked to the
     * given {@link Algorithm}. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a
     * {@link Page} with all entries is queried.
     * <p>
     * The given {@link Algorithm} is identified through its ID given as a parameter. If no {@link Algorithm} with the
     * given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want find linked {@link Publication}s for
     * @param pageable    The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link Publication} entries which are linked to the {@link Algorithm}
     */
    Page<Publication> findLinkedPublications(UUID algorithmId, Pageable pageable);

    /**
     * Retrieve multiple {@link ProblemType}s entries from the database where of {@link ProblemType}s that are linked to
     * the given {@link Algorithm}. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a
     * {@link Page} with all entries is queried.
     * <p>
     * The given {@link Algorithm} is identified through its ID given as a parameter. If no {@link Algorithm} with the
     * given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want find linked {@link ProblemType}s for
     * @param pageable    The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link ProblemType} entries which are linked to the {@link Algorithm}
     */
    Page<ProblemType> findLinkedProblemTypes(UUID algorithmId, Pageable pageable);

    /**
     * Retrieve multiple {@link ApplicationArea}s entries from the database where of {@link ApplicationArea}s that are
     * linked to the given {@link Algorithm}. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a
     * {@link Page} with all entries is queried.
     * <p>
     * The given {@link Algorithm} is identified through its ID given as a parameter. If no {@link Algorithm} with the
     * given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want find linked {@link ApplicationArea}s for
     * @param pageable    The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link ApplicationArea} entries which are linked to the {@link Algorithm}
     */
    Page<ApplicationArea> findLinkedApplicationAreas(UUID algorithmId, Pageable pageable);

    /**
     * Retrieve multiple {@link LearningMethod}s entries from the database where {@link LearningMethod}s that are linked
     * to the given {@link Algorithm}. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a
     * {@link Page} with all entries is queried.
     * <p>
     * The given {@link Algorithm} is identified through its ID given as a parameter. If no {@link Algorithm} with the
     * given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want find linked {@link LearningMethod}s for
     * @param pageable    The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link LearningMethod} entries which are linked to the {@link Algorithm}
     */
    Page<LearningMethod> findLinkedLearningMethods(UUID algorithmId, Pageable pageable);

    /**
     * Checks if a given {@link Publication} is linked to a given {@link Algorithm}.
     * <p>
     * If either the {@link Publication} or the {@link Algorithm} with given IDs could not be found or if a database
     * entry for both could be found but they are not linked a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId   The ID of the {@link Algorithm} we want to check
     * @param publicationId The ID of the {@link Publication} we want to check
     */
    void checkIfPublicationIsLinkedToAlgorithm(UUID algorithmId, UUID publicationId);

    /**
     * Checks if a given {@link ProblemType} is linked to a given {@link Algorithm}.
     * <p>
     * If either the {@link ProblemType} or the {@link Algorithm} with given IDs could not be found or if a database
     * entry for both could be found but they are not linked a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId   The ID of the {@link Algorithm} we want to check
     * @param problemTypeId The ID of the {@link ProblemType} we want to check
     */
    void checkIfProblemTypeIsLinkedToAlgorithm(UUID algorithmId, UUID problemTypeId);

    /**
     * Checks if a given {@link ApplicationArea} is linked to a given {@link Algorithm}.
     * <p>
     * If either the {@link ApplicationArea} or the {@link Algorithm} with given IDs could not be found or if a database
     * entry for both could be found but they are not linked a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId       The ID of the {@link Algorithm} we want to check
     * @param applicationAreaId The ID of the {@link ApplicationArea} we want to check
     */
    void checkIfApplicationAreaIsLinkedToAlgorithm(UUID algorithmId, UUID applicationAreaId);

    /**
     * Checks if a given {@link LearningMethod} is linked to a given {@link Algorithm}.
     * <p>
     * If either the {@link LearningMethod} or the {@link Algorithm} with given IDs could not be found or if a database
     * entry for both could be found but they are not linked a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId      The ID of the {@link Algorithm} we want to check
     * @param learningMethodId The ID of the {@link LearningMethod} we want to check
     * @return {@link LearningMethod} with the provided Id that is linked to the algorithm with the given Id
     */
    LearningMethod getLearningMethodOfAlgorithm(UUID algorithmId, UUID learningMethodId);

    /**
     * Retrieve all {@link Algorithm} revisions from the database.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a {@link Page} with all entries is
     * queried.
     * <p>
     * The given {@link Algorithm} is identified through its ID given as a parameter. If no {@link Algorithm} with the given ID can be found a {@link
     * java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want to find
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link Revision} entries
     */
    Page<Revision<Integer, Algorithm>> findAlgorithmRevisions(UUID algorithmId, Pageable pageable);

    /**
     * Retrieve an {@link Algorithm} revision from the database.
     * <p>
     * If either the {@link Algorithm} or the {@link Revision} with given IDs could not be found or if a database entry for both could be found
     * a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId   The ID of the {@link Algorithm}
     * @param revisionId    The ID of the {@link Revision}
     *
     */
    Revision<Integer, Algorithm> findAlgorithmRevision(UUID algorithmId, Integer revisionId);
}
