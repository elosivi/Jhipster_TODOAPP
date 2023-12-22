package com.ebarbe.service;

import com.ebarbe.domain.*; // for static metamodels
import com.ebarbe.domain.Hierarchy;
import com.ebarbe.repository.HierarchyRepository;
import com.ebarbe.repository.search.HierarchySearchRepository;
import com.ebarbe.service.criteria.HierarchyCriteria;
import com.ebarbe.service.dto.HierarchyDTO;
import com.ebarbe.service.mapper.HierarchyMapper;
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
 * Service for executing complex queries for {@link Hierarchy} entities in the database.
 * The main input is a {@link HierarchyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link HierarchyDTO} or a {@link Page} of {@link HierarchyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class HierarchyQueryService extends QueryService<Hierarchy> {

    private final Logger log = LoggerFactory.getLogger(HierarchyQueryService.class);

    private final HierarchyRepository hierarchyRepository;

    private final HierarchyMapper hierarchyMapper;

    private final HierarchySearchRepository hierarchySearchRepository;

    public HierarchyQueryService(
        HierarchyRepository hierarchyRepository,
        HierarchyMapper hierarchyMapper,
        HierarchySearchRepository hierarchySearchRepository
    ) {
        this.hierarchyRepository = hierarchyRepository;
        this.hierarchyMapper = hierarchyMapper;
        this.hierarchySearchRepository = hierarchySearchRepository;
    }

    /**
     * Return a {@link List} of {@link HierarchyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<HierarchyDTO> findByCriteria(HierarchyCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Hierarchy> specification = createSpecification(criteria);
        return hierarchyMapper.toDto(hierarchyRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link HierarchyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<HierarchyDTO> findByCriteria(HierarchyCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Hierarchy> specification = createSpecification(criteria);
        return hierarchyRepository.findAll(specification, page).map(hierarchyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(HierarchyCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Hierarchy> specification = createSpecification(criteria);
        return hierarchyRepository.count(specification);
    }

    /**
     * Function to convert {@link HierarchyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Hierarchy> createSpecification(HierarchyCriteria criteria) {
        Specification<Hierarchy> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Hierarchy_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Hierarchy_.description));
            }
            if (criteria.getPersonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPersonId(), root -> root.join(Hierarchy_.person, JoinType.LEFT).get(Person_.id))
                    );
            }
        }
        return specification;
    }
}
