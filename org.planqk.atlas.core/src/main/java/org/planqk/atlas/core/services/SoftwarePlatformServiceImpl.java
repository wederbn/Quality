package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private final SoftwarePlatformRepository softwarePlatformRepository;
    private final CloudServiceService cloudServiceService;

    @Transactional
    @Override
    public SoftwarePlatform save(SoftwarePlatform softwarePlatform) {
        // TODO create or update backends when service is implemented
        // softwarePlatform.setSupportedBackends();

        softwarePlatform.setSupportedCloudServices(
                cloudServiceService.createOrUpdateAll(softwarePlatform.getSupportedCloudServices()));

        return this.softwarePlatformRepository.save(softwarePlatform);
    }

    @Override
    public Page<SoftwarePlatform> findAll(Pageable pageable) {
        return softwarePlatformRepository.findAll(pageable);
    }

    @Override
    public SoftwarePlatform findById(UUID platformId) {
        return softwarePlatformRepository.findById(platformId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    @Override
    public void delete(UUID platformId) {
        softwarePlatformRepository.deleteById(platformId);
    }

}
