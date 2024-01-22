package com.ebarbe.repository;

import com.ebarbe.domain.Person;
import java.util.Optional;
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
    @Query("SELECT p FROM Person p WHERE p.user.id = :userId")
    Optional<Person> findOneByUserId(Long userId);
}
