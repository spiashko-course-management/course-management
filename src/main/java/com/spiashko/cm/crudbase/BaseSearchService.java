package com.spiashko.cm.crudbase;


import com.spiashko.cm.crudbase.entity.BaseJournalEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface BaseSearchService<ID, E extends BaseJournalEntity<ID>> {

    List<E> findAll();

    List<E> findAll(Specification<E> spec);

    Optional<E> findOne(ID id);

    Optional<E> findOne(Specification<E> spec);

    E findOneOrThrow(ID id);

    E findOneOrThrow(Specification<E> spec);

}
