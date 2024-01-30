package com.ebarbe.repository;

import com.ebarbe.domain.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Event entity.
 *
 * When extending this class, extend EventRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface EventExtendedRepository {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.eventType where e.id =:eventId")
    Event findOneWithRelationships(@Param("eventId") Long eventId);
}
