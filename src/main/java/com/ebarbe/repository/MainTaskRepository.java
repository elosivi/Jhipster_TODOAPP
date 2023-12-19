package com.ebarbe.repository;

import com.ebarbe.domain.MainTask;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MainTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MainTaskRepository extends JpaRepository<MainTask, Long> {}
