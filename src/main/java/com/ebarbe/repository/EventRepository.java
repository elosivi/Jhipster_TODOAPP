package com.ebarbe.repository;

import com.ebarbe.domain.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Event entity.
 *
 * When extending this class, extend EventRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface EventRepository
    extends EventExtendedRepository, EventRepositoryWithBagRelationships, JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.eventType where e.id =: id")
    default Optional<Event> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Event> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Event> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
