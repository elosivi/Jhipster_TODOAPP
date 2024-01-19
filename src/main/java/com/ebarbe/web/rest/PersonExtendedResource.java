package com.ebarbe.web.rest;

import com.ebarbe.repository.PersonRepository;
import com.ebarbe.service.PersonQueryService;
import com.ebarbe.service.PersonService;
import com.ebarbe.service.dto.PersonDTO;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ebarbe.domain.Person}.
 */
@RestController
@RequestMapping("/api/people/management")
public class PersonExtendedResource extends PersonResource {

    private final Logger log = LoggerFactory.getLogger(PersonExtendedResource.class);

    private final PersonService personService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public PersonExtendedResource(PersonService personService, PersonRepository personRepository, PersonQueryService personQueryService) {
        super(personService, personRepository, personQueryService);
        this.personService = personService;
    }

    /**
     * {@code GET  /people/byUser/:id} : get the person associated to the userId.
     *
     * @param id the id of the personDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<PersonDTO> getPersonByUserId(@PathVariable("userId") Long userId) {
        log.debug("REST request to get Person : {}", userId);
        Optional<PersonDTO> personDTO = personService.findOneByUser(userId);
        return ResponseUtil.wrapOrNotFound(personDTO);
    }

    /**
     * link or unlink an existing user with an existing person
     * @param userId
     * @param personId
     * @return
     */
    @PostMapping("/associate-user/{userId}/with-person/{personId}")
    public ResponseEntity<Void> associateUserWithPerson(@PathVariable Long userId, @PathVariable Long personId) {
        if (userId == null && personId == null) {
            log.debug("REST request to associate User with Person: {} - {} : avorted ", userId, personId);
            return ResponseEntity.ok().build();
        }
        log.debug("REST request to associate User with Person: {} - {}", userId, personId);
        personService.associateUserWithPerson(userId, personId);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createAlert(applicationName, "Association successful", userId.toString()))
            .build();
    }
}
