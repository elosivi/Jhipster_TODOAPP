package com.ebarbe.repository;

import com.ebarbe.domain.RelEventPerson;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 * List of relEventPerson with complete data ( EVENT, PERSON+USER AND HIERARCHY ) : all, all concerninf by an event, all concerning by a person
 * get the one relEventPerson with complete data ( EVENT, PERSON+USER AND HIERARCHY ) concerned with the combination of an event and a person
 * */
public interface RelEventPersonExtendedRepositoryWithBagRelationships {
    static final Logger logger = LoggerFactory.getLogger(RelEventPersonExtendedRepositoryWithBagRelationships.class);

    /**
     * get ALL (EVENT, PERSON+USER AND HIERARCHY )  data for each relEventPerson
     *
     * @param
     * @return a list of RelEventPerson, included all data about entities linked
     */

    @Query(
        "SELECT rep FROM RelEventPerson rep " +
        "JOIN FETCH rep.event e " +
        "JOIN FETCH rep.person p " +
        "JOIN FETCH p.user u " +
        "JOIN FETCH rep.hierarchy h "
    )
    List<RelEventPerson> findAllREPComplete();

    /**
     * get ALL (EVENT, PERSON+USER AND HIERARCHY )  data for each relEventPerson concerning by the event in param
     *
     * @param eventId
     * @return a list of RelEventPerson, included all data about entities linked
     */
    /* @Query(
        "SELECT rep FROM RelEventPerson rep " +
              "JOIN FETCH rep.event e " +
            "JOIN FETCH rep.person p " +
            "JOIN FETCH p.user u " +
            "JOIN FETCH rep.hierarchy h " +
            "WHERE rep.event_id = :eventId"
    )*/

    @Query(
        "SELECT rep FROM RelEventPerson rep " +
        "JOIN FETCH rep.event e " +
        "JOIN FETCH rep.person p " +
        "LEFT JOIN p.user u " +
        "LEFT JOIN rep.hierarchy h " +
        "WHERE rep.event.id = :eventId"
    )
    List<RelEventPerson> findAllREPCompleteByEventId(@Param("eventId") Long eventId);

    /**
     * get ALL (EVENT, PERSON+USER AND HIERARCHY )  data for each relEventPerson concerning by the person in param
     *
     * @param personId
     * @return a list of RelEventPerson, included all data about entities linked
     */
    @Query(
        "SELECT rep FROM RelEventPerson rep " +
        "JOIN FETCH rep.event e " +
        "JOIN FETCH rep.person p " +
        "LEFT JOIN p.user u " +
        "LEFT JOIN rep.hierarchy h " +
        "WHERE rep.person.id = :personId"
    )
    List<RelEventPerson> findAllREPCompleteByPersonId(@Param("personId") Long personId);

    /**
     * get ALL (EVENT, PERSON+USER AND HIERARCHY )  data for each relEventPerson concerning by the event and the hierarchy in param
     *
     * @param eventId,
     * @param hierarchyId,
     * @return a list of RelEventPerson, included all data about entities linked
     */
    @Query(
        "SELECT rep, e, p, u, h FROM RelEventPerson rep " +
        "JOIN Event e ON rep.event.id = e.id " +
        "JOIN Person p ON rep.person.id = p.id " +
        "JOIN User u ON p.user.id = u.id " +
        "JOIN Hierarchy h ON rep.hierarchy.id = h.id " +
        "WHERE rep.event.id = :eventId AND rep.hierarchy.id = :hierarchyId"
    )
    List<RelEventPerson> findAllREPCompleteByEventIdAndHierarchyId(@Param("eventId") Long eventId, @Param("hierarchyId") Long hierarchyId);

    /**
     * get ONE (EVENT, PERSON+USER AND HIERARCHY )  data for each relEventPerson concerning by the event + the person in param
     *
     * @param eventId
     * @param personId
     * @return a list of RelEventPerson, included all data about entities linked
     */
    @Query(
        "SELECT rep FROM RelEventPerson rep " +
        "JOIN FETCH rep.event e " +
        "JOIN FETCH rep.person p " +
        "JOIN FETCH p.user u " +
        "JOIN FETCH rep.hierarchy h " +
        "WHERE rep.event.id = :eventId AND rep.person.id = :personId"
    )
    Optional<RelEventPerson> findREPCompleteByEventIdAndPersonId(@Param("eventId") Long eventId, @Param("personId") Long personId);
}
