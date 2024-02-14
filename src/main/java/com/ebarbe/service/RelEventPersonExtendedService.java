package com.ebarbe.service;

import com.ebarbe.domain.Event;
import com.ebarbe.domain.Hierarchy;
import com.ebarbe.domain.Person;
import com.ebarbe.domain.RelEventPerson;
import com.ebarbe.repository.RelEventPersonExtendedRepository;
import com.ebarbe.repository.RelEventPersonExtendedRepositoryWithBagRelationships;
import com.ebarbe.repository.RelEventPersonRepository;
import com.ebarbe.repository.RelEventPersonRepositoryWithBagRelationships;
import com.ebarbe.repository.search.RelEventPersonSearchRepository;
import com.ebarbe.service.dto.RelEventPersonDTO;
import com.ebarbe.service.mapper.RelEventPersonMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RelEventPerson}.
 */
@Service
@Transactional
public class RelEventPersonExtendedService extends RelEventPersonService {

    private final Logger log = LoggerFactory.getLogger(RelEventPersonExtendedService.class);

    private final RelEventPersonRepository repRepository;

    private final RelEventPersonMapper relEventPersonMapper;

    private final RelEventPersonExtendedRepository repExtRepository;

    private final RelEventPersonExtendedRepositoryWithBagRelationships repExtRepositoryWBR;

    @Override
    public Optional<RelEventPersonDTO> findOne(Long id) {
        return super.findOne(id);
    }

    public RelEventPersonExtendedService(
        RelEventPersonRepository relEventPersonRepository,
        RelEventPersonMapper relEventPersonMapper,
        RelEventPersonSearchRepository relEventPersonSearchRepository,
        RelEventPersonSearchRepository repSearchRepository,
        RelEventPersonExtendedRepository relEventPersonExtendedRepository,
        RelEventPersonExtendedRepository repExtRepository,
        RelEventPersonExtendedRepositoryWithBagRelationships repExtRepositoryWBR
    ) {
        super(relEventPersonRepository, relEventPersonMapper, relEventPersonSearchRepository);
        this.repRepository = relEventPersonRepository;
        this.relEventPersonMapper = relEventPersonMapper;
        this.repExtRepository = repExtRepository;
        this.repExtRepositoryWBR = repExtRepositoryWBR;
    }

    /**
     * Get all the relEventPeople with data about relations (person, user, event, hierarchy).
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RelEventPersonDTO> findAllREPComplete(Pageable pageable) {
        log.debug("Request to get all RelEventPeople");
        List<RelEventPerson> resultList = repExtRepositoryWBR.findAllREPComplete();
        long totalElements = resultList.size();

        return paginateResults(resultList, pageable, totalElements).map(relEventPersonMapper::toDto);
    }

    /**
     * Get all the relEventPeople with data about relations (person, user, event, hierarchy).
     * concerned by the event in param
     * @param pageable
     * @param eventId
     * @return
     */
    @Transactional(readOnly = true)
    public Page<RelEventPersonDTO> findAllREPCompleteByEventId(Pageable pageable, Long eventId) {
        log.debug("Request to get all RelEventPeople concerned by event :" + eventId);
        List<RelEventPerson> resultList = repExtRepositoryWBR.findAllREPCompleteByEventId(eventId);
        long totalElements = resultList.size();

        return paginateResults(resultList, pageable, totalElements).map(relEventPersonMapper::toDto);
    }

    /**
     * Get all the relEventPeople with data about relations (person, user, event, hierarchy).
     * concerned by the person in param
     * @param pageable
     * @param personId
     * @return
     */
    @Transactional(readOnly = true)
    public Page<RelEventPersonDTO> findAllREPCompleteByPersonId(Pageable pageable, Long personId) {
        log.debug("Request to get all RelEventPeople concerned by person : {}", personId);
        List<RelEventPerson> resultList = repExtRepositoryWBR.findAllREPCompleteByPersonId(personId);
        long totalElements = resultList.size();

        return paginateResults(resultList, pageable, totalElements).map(relEventPersonMapper::toDto);
    }

    /**
     * Get all the relEventPeople with data about relations (person, user, event, hierarchy).
     * concerned by the event AND the hierarchy in param
     * @param pageable
     * @param event
     * @param hierarchy
     * @return
     */
    @Transactional(readOnly = true)
    public Page<RelEventPersonDTO> findAllREPCompleteByEventIdAndHierarchyId(Pageable pageable, Event event, Hierarchy hierarchy) {
        log.debug(
            "Request to get all RelEventPeople concerned by event :" + event.getLabel() + " and hierarchy: " + hierarchy.getDescription()
        );
        List<RelEventPerson> resultList = repExtRepositoryWBR.findAllREPCompleteByEventIdAndHierarchyId(event.getId(), hierarchy.getId());
        long totalElements = resultList.size();

        return paginateResults(resultList, pageable, totalElements).map(relEventPersonMapper::toDto);
    }

    /**
     * The only one method to return an unique relEventPerson
     * @param eventId
     * @param personId
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<RelEventPersonDTO> findREPCompleteByEventIdAndPersonId(Long eventId, Long personId) {
        log.debug("Request to get all RelEventPeople concerned by event :" + eventId + " and person: " + personId);
        Optional<RelEventPerson> result = repExtRepositoryWBR.findREPCompleteByEventIdAndPersonId(eventId, personId);

        return result.map(relEventPersonMapper::toDto);
    }

    /**
     * @param resultList
     * @param pageable
     * @param totalElements
     * @return page of RelEventPerson
     */
    private Page<RelEventPerson> paginateResults(List<RelEventPerson> resultList, Pageable pageable, long totalElements) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > totalElements ? (int) totalElements : (start + pageable.getPageSize());

        return new PageImpl<>(resultList.subList(start, end), pageable, totalElements);
    }

    //DELETE ONE EVENT + PERSON
    /* public void deleteRelEventPerson(Event event, Person person) {
        log.debug("Request to delete RelEventPerson concerned by event : {} and person: {}", event.getId(), person.getId());
        try {
            repExtRepository.deleteRelEventPerson(event.getId(), person.getId());
        } catch (Exception ex) {
            log.error("Une erreur s'est produite : " + ex.getMessage());
            throw new EntityNotFoundException(
                "DELETE : RelEventPerson concerned by event : " + event.getId() + " and person: " + person.getId() + " was not found."
            );
        }
    }*/

    //DELETE ALL BY EVENT
    /* public void deleteAllByEventId(Event event, Person person) {
        log.debug("Request to delete RelEventPerson concerned by event : {} and person: {}", event.getId(), person.getId());
        try {
            repExtRepository.deleteAllByEventId(event.getId());
        } catch (Exception ex) {
            log.error("Une erreur s'est produite : " + ex.getMessage());
            throw new EntityNotFoundException("DELETE : no RelEventPerson concerned by event : " + event.getId() + " was found.");
        }
    }
*/
    //DELETE BY PERSON
    /*public void deleteAllByPersonId(Person person) {
        log.debug("Request to delete RelEventPerson concerned by person: {}", person.getId());
        try {
            repExtRepository.deleteAllByPersonId(person.getId());
        } catch (Exception ex) {
            log.error("Une erreur s'est produite : " + ex.getMessage());
            throw new EntityNotFoundException("DELETE : no RelEventPerson concerned by person : " + person.getId() + " was found.");
        }
    }*/

    //UPDATE ONE BY EVENT + PERSON

    /**
     * EventId and PersonId are the key to found the relEventperson to change
     * @param EventId about the relEventPerson to change
     * @param PersonId about the relEventPerson to change
     * @param newRelEventPersonDTO new data to register
     * @return
     */
    public RelEventPersonDTO update(Long EventId, Long PersonId, RelEventPersonDTO newRelEventPersonDTO) {
        log.debug("Request to update to delete RelEventPerson concerned by event : {} and person: {}", EventId, PersonId);

        RelEventPerson relEventPersonNew = relEventPersonMapper.toEntity(newRelEventPersonDTO);

        try {
            repExtRepository.updateRelEventPerson(
                EventId,
                PersonId,
                relEventPersonNew.getEvent().getId(),
                relEventPersonNew.getPerson().getId(),
                relEventPersonNew.getHierarchy().getId(),
                relEventPersonNew.getParticipation()
            );
            return relEventPersonMapper.toDto(relEventPersonNew);
            //repSearchRepository.index(relEventPerson); //=> TODO modifier la méthode qui attend un id

        } catch (Exception ex) {
            log.error("Une erreur s'est produite : " + ex.getMessage());
            throw new EntityNotFoundException(
                "UPDATE : RelEventPerson concerned by event : " + EventId + " and person: " + PersonId + " was not found."
            );
        }
    }
}
