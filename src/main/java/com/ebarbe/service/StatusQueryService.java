package com.ebarbe.service;

import com.ebarbe.domain.*; // for static metamodels
import com.ebarbe.domain.Status;
import com.ebarbe.repository.StatusRepository;
import com.ebarbe.repository.search.StatusSearchRepository;
import com.ebarbe.service.criteria.StatusCriteria;
import com.ebarbe.service.dto.StatusDTO;
import com.ebarbe.service.mapper.StatusMapper;
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
 * Service for executing complex queries for {@link Status} entities in the database.
 * The main input is a {@link StatusCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StatusDTO} or a {@link Page} of {@link StatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StatusQueryService extends QueryService<Status> {

    private final Logger log = LoggerFactory.getLogger(StatusQueryService.class);

    private final StatusRepository statusRepository;

    private final StatusMapper statusMapper;

    private final StatusSearchRepository statusSearchRepository;

    public StatusQueryService(StatusRepository statusRepository, StatusMapper statusMapper, StatusSearchRepository statusSearchRepository) {
        this.statusRepository = statusRepository;
        this.statusMapper = statusMapper;
        this.statusSearchRepository = statusSearchRepository;
    }

    /**
     * Return a {@link List} of {@link StatusDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StatusDTO> findByCriteria(StatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Status> specification = createSpecification(criteria);
        return statusMapper.toDto(statusRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StatusDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StatusDTO> findByCriteria(StatusCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Status> specification = createSpecification(criteria);
        return statusRepository.findAll(specification, page).map(statusMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StatusCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Status> specification = createSpecification(criteria);
        return statusRepository.count(specification);
    }

    /**
     * Function to convert {@link StatusCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Status> createSpecification(StatusCriteria criteria) {
        Specification<Status> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Status_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Status_.description));
            }
            if (criteria.getMainTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getMainTaskId(), root -> root.join(Status_.mainTasks, JoinType.LEFT).get(MainTask_.id))
                    );
            }
            if (criteria.getSubTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSubTaskId(), root -> root.join(Status_.subTasks, JoinType.LEFT).get(SubTask_.id))
                    );
            }
        }
        return specification;
    }
}
