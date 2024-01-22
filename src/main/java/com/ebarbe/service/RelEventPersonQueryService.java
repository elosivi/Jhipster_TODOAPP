package com.ebarbe.service;

import com.ebarbe.domain.*; // for static metamodels
import com.ebarbe.domain.RelEventPerson;
import com.ebarbe.repository.RelEventPersonRepository;
import com.ebarbe.repository.search.RelEventPersonSearchRepository;
import com.ebarbe.service.criteria.RelEventPersonCriteria;
import com.ebarbe.service.dto.RelEventPersonDTO;
import com.ebarbe.service.mapper.RelEventPersonMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RelEventPerson} entities in the database.
 * The main input is a {@link RelEventPersonCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RelEventPersonDTO} or a {@link Page} of {@link RelEventPersonDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RelEventPersonQueryService extends QueryService<RelEventPerson> {

    private final Logger log = LoggerFactory.getLogger(RelEventPersonQueryService.class);

    private final RelEventPersonRepository relEventPersonRepository;

    private final RelEventPersonMapper relEventPersonMapper;

    private final RelEventPersonSearchRepository relEventPersonSearchRepository;

    public RelEventPersonQueryService(
        RelEventPersonRepository relEventPersonRepository,
        RelEventPersonMapper relEventPersonMapper,
        RelEventPersonSearchRepository relEventPersonSearchRepository
    ) {
        this.relEventPersonRepository = relEventPersonRepository;
        this.relEventPersonMapper = relEventPersonMapper;
        this.relEventPersonSearchRepository = relEventPersonSearchRepository;
    }

    /**
     * Return a {@link List} of {@link RelEventPersonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RelEventPersonDTO> findByCriteria(RelEventPersonCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<RelEventPerson> specification = createSpecification(criteria);
        return relEventPersonMapper.toDto(relEventPersonRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RelEventPersonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RelEventPersonDTO> findByCriteria(RelEventPersonCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RelEventPerson> specification = createSpecification(criteria);
        return relEventPersonRepository.findAll(specification, page).map(relEventPersonMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RelEventPersonCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<RelEventPerson> specification = createSpecification(criteria);
        return relEventPersonRepository.count(specification);
    }

    /**
     * Function to convert {@link RelEventPersonCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RelEventPerson> createSpecification(RelEventPersonCriteria criteria) {
        Specification<RelEventPerson> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getParticipation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getParticipation(), RelEventPerson_.participation));
            }
            if (criteria.getEventId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEventId(), root -> root.join(RelEventPerson_.event, JoinType.LEFT).get(Event_.id))
                    );
            }
            if (criteria.getPersonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPersonId(), root -> root.join(RelEventPerson_.person, JoinType.LEFT).get(Person_.id))
                    );
            }
            if (criteria.getHierarchyId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getHierarchyId(),
                            root -> root.join(RelEventPerson_.hierarchy, JoinType.LEFT).get(Hierarchy_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
