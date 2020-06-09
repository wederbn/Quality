package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.repository.PatternRelationTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PatternRelationTypeServiceImpl implements PatternRelationTypeService {

    private PatternRelationTypeRepository repo;
    private final String NO_TYPE_ERROR = "PatternRelationType does not exist!";

    @Override
    public PatternRelationType save(PatternRelationType type) {
        return repo.save(type);
    }

    @Override
    public PatternRelationType findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException(NO_TYPE_ERROR));
    }

    @Override
    public Page<PatternRelationType> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public PatternRelationType update(UUID id, PatternRelationType type) {
        PatternRelationType persistedType = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_TYPE_ERROR));
        persistedType.setName(type.getName());
        return repo.save(persistedType);
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

}