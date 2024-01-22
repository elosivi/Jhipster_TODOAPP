package com.ebarbe.service;

import com.ebarbe.domain.Person;
import com.ebarbe.repository.PersonExtendedRepository;
import com.ebarbe.repository.PersonRepository;
import com.ebarbe.repository.UserRepository;
import com.ebarbe.repository.search.PersonSearchRepository;
import com.ebarbe.service.dto.PersonDTO;
import com.ebarbe.service.dto.UserDTO;
import com.ebarbe.service.mapper.PersonMapper;
import com.ebarbe.service.mapper.UserMapper;
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
    private final PersonExtendedRepository personExtendedRepository;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    private final PersonSearchRepository personSearchRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public PersonService(
        PersonExtendedRepository personExtendedRepository,
        PersonRepository personRepository,
        PersonMapper personMapper,
        PersonSearchRepository personSearchRepository,
        UserRepository userRepository,
        UserMapper userMapper
    ) {
        this.personExtendedRepository = personExtendedRepository;
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.personSearchRepository = personSearchRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
     * Get all the people with user info.
     * added by ebarbe
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PersonDTO> findAllWithUser(Pageable pageable) {
        log.debug("Request to get all People with user informations");
        Page<Person> persons = personRepository.findAll(pageable);
        return persons.map(person -> {
            PersonDTO personDTO = personMapper.toDto(person);

            // Chargez les informations de l'utilisateur
            if (person.getUser() != null) {
                UserDTO userDTO = userMapper.userToUserDTO(userRepository.findById(person.getUser().getId()).orElse(null));
                personDTO.setUser(userDTO);
            }

            return personDTO;
        });
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

    /**
     * Link or unlink a user with a person
     * if the two are not null : link the two
     * if one is null : unlink
     * if the two are null: do nothing
     * @param userId
     * @param personId
     */
    public void associateUserWithPerson(Long userId, Long personId) {
        Person person = null;
        if (userId != null || personId != null) {
            if (userId != null && personId != null) {
                person =
                    personRepository
                        .findById(personId)
                        .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + personId));
                UserDTO userDTO = userRepository
                    .findById(userId)
                    .map(userMapper::userToUserDTO)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

                person.setUser(userMapper.userDTOToSimpleUser(userDTO));
            }
            if (userId != null && personId == null) {
                person =
                    this.personExtendedRepository.findOneByUserId(userId)
                        .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + personId));

                person.setUser(null);
            }
            if (userId == null && personId != null) {
                person =
                    personRepository
                        .findById(personId)
                        .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + personId));
                person.setUser(null);
            }
            personRepository.save(person);
        }
    }

    @Transactional(readOnly = true)
    public Optional<PersonDTO> findOneByUser(Long id) {
        log.debug("Request to get Person associated with user : {}", id);
        return this.personExtendedRepository.findOneByUserId(id).map(personMapper::toDto);
    }
}
