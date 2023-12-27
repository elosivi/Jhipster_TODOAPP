package com.ebarbe.repository;

import com.ebarbe.domain.Person;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Person entity.
 */
@SuppressWarnings("unused")
@Repository
@Qualifier("personExtendedRepository")
public interface PersonExtendedRepository extends PersonRepository {
    /*  @Query("SELECT p FROM person p left JOIN jhi_user u ON p.user_id = u.id")
    Page<Person> findAllWithUser(Pageable pageable);
*/
}
