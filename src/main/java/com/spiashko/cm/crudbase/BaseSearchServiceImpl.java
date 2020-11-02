package com.spiashko.cm.crudbase;


import com.spiashko.cm.crudbase.entity.BaseJournalEntity;
import com.spiashko.cm.crudbase.repository.BaseJournalRepository;
import net.jodah.typetools.TypeResolver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public abstract class BaseSearchServiceImpl<
    ID,
    E extends BaseJournalEntity<ID>,
    R extends BaseJournalRepository<ID, E>>
    implements BaseSearchService<ID, E> {

    private final R repository;
    private final Class<E> persistentClass;

    protected BaseSearchServiceImpl(R repository) {
        this.repository = repository;

        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(BaseJournalRepository.class, repository.getClass());
        this.persistentClass = (Class<E>) typeArguments[0];
    }

    @Transactional(readOnly = true)
    @Override
    public List<E> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<E> findAll(Specification<E> spec) {
        return repository.findAll(spec);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<E> findOne(ID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<E> findOne(Specification<E> spec) {
        return repository.findOne(spec);
    }

    @Transactional(readOnly = true)
    @Override
    public E findOneOrThrow(ID id) {
        Optional<E> result = findOne(id);
        if (!result.isPresent()) {
            throw new EntityNotFoundException(
                String.format("No %s entity with id %s exists!", persistentClass.getSimpleName(), id));
        }
        return result.get();
    }

    @Transactional(readOnly = true)
    @Override
    public E findOneOrThrow(Specification<E> spec) {
        Optional<E> result = findOne(spec);
        if (!result.isPresent()) {
            throw new EntityNotFoundException(
                String.format("No %s entity with spec %s exists!", persistentClass.getSimpleName(), spec));
        }
        return result.get();
    }

    protected R getRepository() {
        return repository;
    }
}
