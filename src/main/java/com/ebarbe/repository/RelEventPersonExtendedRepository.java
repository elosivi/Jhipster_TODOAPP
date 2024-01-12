package com.ebarbe.repository;

import com.ebarbe.domain.RelEventPerson;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelEventPersonExtendedRepository {
    /**
     * Update a relEventPerson concerned by event and person in param
     * @param eventId
     * @param personId
     * @param newValue
     */
    @Modifying
    @Query(
        "UPDATE RelEventPerson rep " +
        "SET rep.event.id = :newEventId, " +
        "rep.person.id = :newPersonId, " +
        "rep.hierarchy.id = :newHierarchyId, " +
        "rep.participation = :newParticipation " +
        "WHERE rep.event.id = :eventId AND rep.person.id = :personId"
    )
    void updateRelEventPerson(
        @Param("eventId") Long eventId,
        @Param("personId") Long personId,
        @Param("newEventId") Long newEventId,
        @Param("newPersonId") Long newPersonId,
        @Param("newHierarchyId") Long newHierarchyId,
        @Param("newParticipation") String newParticipation
    );

    /**
     * remove a relEventPerson concerned by event and person in param
     * @param eventId
     * @param personId
     */
    @Modifying
    @Query("DELETE FROM RelEventPerson rep " + "WHERE rep.event.id = :eventId AND rep.person.id = :personId")
    void deleteRelEventPerson(@Param("eventId") Long eventId, @Param("personId") Long personId);

    /**
     * remove all relEventPerson concerned by event in param
     * @param eventId
     */
    @Modifying
    @Query("DELETE FROM RelEventPerson rep " + "WHERE rep.event.id = :eventId")
    void deleteAllByEventId(@Param("eventId") Long eventId);

    /**
     * remove all relEventPerson concerned by person in param
     * @param personId
     */
    @Modifying
    @Query("DELETE FROM RelEventPerson rep " + "WHERE rep.person.id = :personId")
    void deleteAllByPersonId(@Param("personId") Long personId);
}
