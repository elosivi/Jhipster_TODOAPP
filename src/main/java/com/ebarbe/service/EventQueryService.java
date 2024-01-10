package com.ebarbe.service;

import com.ebarbe.domain.*; // for static metamodels
import com.ebarbe.domain.Event;
import com.ebarbe.repository.EventRepository;
import com.ebarbe.repository.search.EventSearchRepository;
import com.ebarbe.service.criteria.EventCriteria;
import com.ebarbe.service.dto.EventDTO;
import com.ebarbe.service.mapper.EventMapper;
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
 * Service for executing complex queries for {@link Event} entities in the database.
 * The main input is a {@link EventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventDTO} or a {@link Page} of {@link EventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventQueryService extends QueryService<Event> {

    private final Logger log = LoggerFactory.getLogger(EventQueryService.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final EventSearchRepository eventSearchRepository;

    public EventQueryService(EventRepository eventRepository, EventMapper eventMapper, EventSearchRepository eventSearchRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventSearchRepository = eventSearchRepository;
    }

    /**
     * Return a {@link List} of {@link EventDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> findByCriteria(EventCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Event> specification = createSpecification(criteria);
        return eventMapper.toDto(eventRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EventDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> findByCriteria(EventCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.findAll(specification, page).map(eventMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EventCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.count(specification);
    }

    /**
     * Function to convert {@link EventCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Event> createSpecification(EventCriteria criteria) {
        Specification<Event> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Event_.id));
            }
            if (criteria.getLabel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLabel(), Event_.label));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Event_.description));
            }
            if (criteria.getTheme() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTheme(), Event_.theme));
            }
            if (criteria.getDateStart() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateStart(), Event_.dateStart));
            }
            if (criteria.getDateEnd() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateEnd(), Event_.dateEnd));
            }
            if (criteria.getPlace() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPlace(), Event_.place));
            }
            if (criteria.getPlaceDetails() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPlaceDetails(), Event_.placeDetails));
            }
            if (criteria.getAdress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAdress(), Event_.adress));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), Event_.note));
            }
            if (criteria.getEventTypeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEventTypeId(), root -> root.join(Event_.eventType, JoinType.LEFT).get(EventType_.id))
                    );
            }
            if (criteria.getPersonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPersonId(), root -> root.join(Event_.people, JoinType.LEFT).get(Person_.id))
                    );
            }
            if (criteria.getRelEventPersonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRelEventPersonId(),
                            root -> root.join(Event_.relEventPeople, JoinType.LEFT).get(RelEventPerson_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
