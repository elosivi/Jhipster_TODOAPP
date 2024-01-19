package com.ebarbe.service;

import com.ebarbe.domain.*; // for static metamodels
import com.ebarbe.domain.MainTask;
import com.ebarbe.repository.MainTaskRepository;
import com.ebarbe.service.criteria.MainTaskCriteria;
import com.ebarbe.service.dto.MainTaskDTO;
import com.ebarbe.service.mapper.MainTaskMapper;
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
 * Service for executing complex queries for {@link MainTask} entities in the database.
 * The main input is a {@link MainTaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MainTaskDTO} or a {@link Page} of {@link MainTaskDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MainTaskQueryService extends QueryService<MainTask> {

    private final Logger log = LoggerFactory.getLogger(MainTaskQueryService.class);

    private final MainTaskRepository mainTaskRepository;

    private final MainTaskMapper mainTaskMapper;

    public MainTaskQueryService(MainTaskRepository mainTaskRepository, MainTaskMapper mainTaskMapper) {
        this.mainTaskRepository = mainTaskRepository;
        this.mainTaskMapper = mainTaskMapper;
    }

    /**
     * Return a {@link List} of {@link MainTaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MainTaskDTO> findByCriteria(MainTaskCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<MainTask> specification = createSpecification(criteria);
        return mainTaskMapper.toDto(mainTaskRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MainTaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MainTaskDTO> findByCriteria(MainTaskCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MainTask> specification = createSpecification(criteria);
        return mainTaskRepository.findAll(specification, page).map(mainTaskMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MainTaskCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<MainTask> specification = createSpecification(criteria);
        return mainTaskRepository.count(specification);
    }

    /**
     * Function to convert {@link MainTaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MainTask> createSpecification(MainTaskCriteria criteria) {
        Specification<MainTask> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), MainTask_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), MainTask_.description));
            }
            if (criteria.getDeadline() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeadline(), MainTask_.deadline));
            }
            if (criteria.getCreation() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreation(), MainTask_.creation));
            }
            if (criteria.getCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCost(), MainTask_.cost));
            }
            if (criteria.getCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCategoryId(), root -> root.join(MainTask_.category, JoinType.LEFT).get(Category_.id))
                    );
            }
            if (criteria.getPersonOwnerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPersonOwnerId(),
                            root -> root.join(MainTask_.personOwner, JoinType.LEFT).get(Person_.id)
                        )
                    );
            }
            if (criteria.getStatusId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStatusId(), root -> root.join(MainTask_.status, JoinType.LEFT).get(Status_.id))
                    );
            }
            if (criteria.getSubTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSubTaskId(), root -> root.join(MainTask_.subTasks, JoinType.LEFT).get(SubTask_.id))
                    );
            }
        }
        return specification;
    }
}
