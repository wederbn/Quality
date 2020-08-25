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

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private final SoftwarePlatformRepository softwarePlatformRepository;

    private final ImplementationRepository implementationRepository;

    private final ComputeResourceRepository computeResourceRepository;

    private final CloudServiceRepository cloudServiceRepository;

    @Override
    public Page<SoftwarePlatform> searchAllByName(String name, Pageable p) {
        return softwarePlatformRepository.findAllByNameContainingIgnoreCase(name, p);
    }

    @Override
    @Transactional
    public SoftwarePlatform create(SoftwarePlatform softwarePlatform) {
        return this.softwarePlatformRepository.save(softwarePlatform);
    }

    @Override
    public Page<SoftwarePlatform> findAll(Pageable pageable) {
        return softwarePlatformRepository.findAll(pageable);
    }

    @Override
    public SoftwarePlatform findById(UUID softwarePlatformId) {
        return softwarePlatformRepository.findById(softwarePlatformId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public SoftwarePlatform update(SoftwarePlatform softwarePlatform) {
        SoftwarePlatform persistedSoftwarePlatform = findById(softwarePlatform.getId());

        persistedSoftwarePlatform.setName(softwarePlatform.getName());
        persistedSoftwarePlatform.setLink(softwarePlatform.getLink());
        persistedSoftwarePlatform.setLicence(softwarePlatform.getLicence());
        persistedSoftwarePlatform.setVersion(softwarePlatform.getVersion());

        return softwarePlatformRepository.save(softwarePlatform);
    }

    @Override
    @Transactional
    public void delete(UUID softwarePlatformId) {
        SoftwarePlatform softwarePlatform = findById(softwarePlatformId);

        removeReferences(softwarePlatform);

        softwarePlatformRepository.deleteById(softwarePlatformId);
    }

    private void removeReferences(SoftwarePlatform softwarePlatform) {
        softwarePlatform.getImplementations().forEach(
                implementation -> implementation.removeSoftwarePlatform(softwarePlatform));
        softwarePlatform.getSupportedCloudServices().forEach(
                cloudService -> cloudService.removeSoftwarePlatform(softwarePlatform));
        softwarePlatform.getSupportedComputeResources().forEach(
                computeResource -> computeResource.removeSoftwarePlatform(softwarePlatform));
    }

    @Override
    public Page<Implementation> findImplementations(UUID softwarePlatformId, Pageable pageable) {
        if (!softwarePlatformRepository.existsSoftwarePlatformById(softwarePlatformId)) {
            throw new NoSuchElementException();
        }

        return implementationRepository.findImplementationsBySoftwarePlatformId(softwarePlatformId, pageable);
    }

    @Override
    public Page<CloudService> findCloudServices(UUID softwarePlatformId, Pageable pageable) {
        if (!softwarePlatformRepository.existsSoftwarePlatformById(softwarePlatformId)) {
            throw new NoSuchElementException();
        }

        return cloudServiceRepository.findCloudServicesBySoftwarePlatformId(softwarePlatformId, pageable);
    }

    @Override
    public Page<ComputeResource> findComputeResources(UUID softwarePlatformId, Pageable pageable) {
        if (!softwarePlatformRepository.existsSoftwarePlatformById(softwarePlatformId)) {
            throw new NoSuchElementException();
        }

        return computeResourceRepository.findComputeResourcesBySoftwarePlatformId(softwarePlatformId, pageable);
    }
}
