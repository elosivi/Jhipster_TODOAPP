package com.ebarbe.repository;

import com.ebarbe.domain.RelEventPerson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RelEventPerson entity.
 *
 * When extending this class, extend RelEventPersonRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface RelEventPersonRepository
    extends
        RelEventPersonRepositoryWithBagRelationships,
        RelEventPersonExtendedRepositoryWithBagRelationships,
        RelEventPersonExtendedRepository,
        JpaRepository<RelEventPerson, Long>,
        JpaSpecificationExecutor<RelEventPerson> {
    /**
     * @param id
     * @return
     */
    default Optional<RelEventPerson> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<RelEventPerson> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<RelEventPerson> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
