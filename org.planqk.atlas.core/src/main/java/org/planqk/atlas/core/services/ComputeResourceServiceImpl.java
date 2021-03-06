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

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.planqk.atlas.core.util.CollectionUtils;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ComputeResourceServiceImpl implements ComputeResourceService {

    private final ComputeResourceRepository computeResourceRepository;

    private final CloudServiceRepository cloudServiceRepository;

    private final SoftwarePlatformRepository softwarePlatformRepository;

    private final ComputeResourcePropertyService computeResourcePropertyService;

    @Override
    public Page<ComputeResource> searchAllByName(String name, @NonNull Pageable pageable) {
        return computeResourceRepository.findAllByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    @Transactional
    public ComputeResource create(@NonNull ComputeResource computeResource) {
        return computeResourceRepository.save(computeResource);
    }

    @Override
    public Page<ComputeResource> findAll(@NonNull Pageable pageable) {
        return computeResourceRepository.findAll(pageable);
    }

    @Override
    public ComputeResource findById(@NonNull UUID computeResourceId) {
        return ServiceUtils.findById(computeResourceId, ComputeResource.class, computeResourceRepository);
    }

    @Override
    @Transactional
    public ComputeResource update(@NonNull ComputeResource computeResource) {
        final ComputeResource persistedComputeResource = findById(computeResource.getId());

        persistedComputeResource.setName(computeResource.getName());
        persistedComputeResource.setVendor(computeResource.getVendor());
        persistedComputeResource.setTechnology(computeResource.getTechnology());
        persistedComputeResource.setQuantumComputationModel(computeResource.getQuantumComputationModel());

        return computeResourceRepository.save(persistedComputeResource);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID computeResourceId) {
        ServiceUtils.throwIfNotExists(computeResourceId, ComputeResource.class, computeResourceRepository);

        if ((cloudServiceRepository.countCloudServiceByComputeResource(computeResourceId) +
                softwarePlatformRepository.countSoftwarePlatformByComputeResource(computeResourceId)) > 0) {
            throw new EntityReferenceConstraintViolationException(
                    "ComputeResource with ID \"" + computeResourceId + "\" cannot be deleted, " +
                            "because it is still in linked to existing software platforms or cloud services");
        }

        final ComputeResource computeResource = findById(computeResourceId);

        removeReferences(computeResource);

        computeResourceRepository.deleteById(computeResourceId);
    }

    private void removeReferences(@NonNull ComputeResource computeResource) {
        CollectionUtils.forEachOnCopy(computeResource.getProvidedComputingResourceProperties(),
                property -> computeResourcePropertyService.delete(property.getId()));
    }

    @Override
    public Page<CloudService> findLinkedCloudServices(@NonNull UUID computeResourceId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(computeResourceId, ComputeResource.class, computeResourceRepository);
        return cloudServiceRepository.findCloudServicesByComputeResourceId(computeResourceId, pageable);
    }

    @Override
    public Page<SoftwarePlatform> findLinkedSoftwarePlatforms(@NonNull UUID computeResourceId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(computeResourceId, ComputeResource.class, computeResourceRepository);
        return softwarePlatformRepository.findSoftwarePlatformsByComputeResourceId(computeResourceId, pageable);
    }
}
