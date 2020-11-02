package com.spiashko.cm.crudbase.repository;


import com.spiashko.cm.crudbase.entity.BaseJournalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseJournalRepository<ID, T extends BaseJournalEntity<ID>>
    extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}
