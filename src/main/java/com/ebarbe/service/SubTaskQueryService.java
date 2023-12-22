package com.ebarbe.service;

import com.ebarbe.domain.*; // for static metamodels
import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.SubTaskRepository;
import com.ebarbe.repository.search.SubTaskSearchRepository;
import com.ebarbe.service.criteria.SubTaskCriteria;
import com.ebarbe.service.dto.SubTaskDTO;
import com.ebarbe.service.mapper.SubTaskMapper;
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
 * Service for executing complex queries for {@link SubTask} entities in the database.
 * The main input is a {@link SubTaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SubTaskDTO} or a {@link Page} of {@link SubTaskDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SubTaskQueryService extends QueryService<SubTask> {

    private final Logger log = LoggerFactory.getLogger(SubTaskQueryService.class);

    private final SubTaskRepository subTaskRepository;

    private final SubTaskMapper subTaskMapper;

    private final SubTaskSearchRepository subTaskSearchRepository;

    public SubTaskQueryService(
        SubTaskRepository subTaskRepository,
        SubTaskMapper subTaskMapper,
        SubTaskSearchRepository subTaskSearchRepository
    ) {
        this.subTaskRepository = subTaskRepository;
        this.subTaskMapper = subTaskMapper;
        this.subTaskSearchRepository = subTaskSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SubTaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SubTaskDTO> findByCriteria(SubTaskCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SubTask> specification = createSpecification(criteria);
        return subTaskMapper.toDto(subTaskRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SubTaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SubTaskDTO> findByCriteria(SubTaskCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SubTask> specification = createSpecification(criteria);
        return subTaskRepository.findAll(specification, page).map(subTaskMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SubTaskCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SubTask> specification = createSpecification(criteria);
        return subTaskRepository.count(specification);
    }

    /**
     * Function to convert {@link SubTaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SubTask> createSpecification(SubTaskCriteria criteria) {
        Specification<SubTask> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SubTask_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), SubTask_.description));
            }
            if (criteria.getDeadline() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeadline(), SubTask_.deadline));
            }
            if (criteria.getCreation() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreation(), SubTask_.creation));
            }
            if (criteria.getCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCost(), SubTask_.cost));
            }
            if (criteria.getMainTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getMainTaskId(), root -> root.join(SubTask_.mainTask, JoinType.LEFT).get(MainTask_.id))
                    );
            }
            if (criteria.getPersonDoerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPersonDoerId(),
                            root -> root.join(SubTask_.personDoer, JoinType.LEFT).get(Person_.id)
                        )
                    );
            }
            if (criteria.getStatusId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStatusId(), root -> root.join(SubTask_.status, JoinType.LEFT).get(Status_.id))
                    );
            }
        }
        return specification;
    }
}
