package com.ebarbe.service;

import com.ebarbe.domain.Person;
import com.ebarbe.repository.PersonRepository;
import com.ebarbe.repository.search.PersonSearchRepository;
import com.ebarbe.service.dto.PersonDTO;
import com.ebarbe.service.mapper.PersonMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ebarbe.domain.Person}.
 */
@Service
@Transactional
public class PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final PersonSearchRepository personSearchRepository;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper, PersonSearchRepository personSearchRepository) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.personSearchRepository = personSearchRepository;
    }

    /**
     * Save a person.
     *
     * @param personDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonDTO save(PersonDTO personDTO) {
        log.debug("Request to save Person : {}", personDTO);
        Person person = personMapper.toEntity(personDTO);
        person = personRepository.save(person);
        PersonDTO result = personMapper.toDto(person);
        personSearchRepository.index(person);
        return result;
    }

    /**
     * Update a person.
     *
     * @param personDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonDTO update(PersonDTO personDTO) {
        log.debug("Request to update Person : {}", personDTO);
        Person person = personMapper.toEntity(personDTO);
        person = personRepository.save(person);
        PersonDTO result = personMapper.toDto(person);
        personSearchRepository.index(person);
        return result;
    }

    /**
     * Partially update a person.
     *
     * @param personDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PersonDTO> partialUpdate(PersonDTO personDTO) {
        log.debug("Request to partially update Person : {}", personDTO);

        return personRepository
            .findById(personDTO.getId())
            .map(existingPerson -> {
                personMapper.partialUpdate(existingPerson, personDTO);

                return existingPerson;
            })
            .map(personRepository::save)
            .map(savedPerson -> {
                personSearchRepository.index(savedPerson);
                return savedPerson;
            })
            .map(personMapper::toDto);
    }

    /**
     * Get all the people.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PersonDTO> findAll(Pageable pageable) {
        log.debug("Request to get all People");
        return personRepository.findAll(pageable).map(personMapper::toDto);
    }

    /**
     * Get one person by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PersonDTO> findOne(Long id) {
        log.debug("Request to get Person : {}", id);
        return personRepository.findById(id).map(personMapper::toDto);
    }

    /**
     * Delete the person by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Person : {}", id);
        personRepository.deleteById(id);
        personSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the person corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PersonDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of People for query {}", query);
        return personSearchRepository.search(query, pageable).map(personMapper::toDto);
    }
}
