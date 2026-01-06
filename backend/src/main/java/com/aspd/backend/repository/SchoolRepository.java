package com.aspd.backend.repository;

import com.aspd.backend.model.School;
import com.aspd.backend.model.SchoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByNameIgnoreCase(String name);

    List<School> findAllByStatus(SchoolStatus status);
}
