package com.mysite.sbb.main;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckcsRepository extends JpaRepository<Checkcs, Long> {
    Optional<Checkcs> findByYear(String year);
}
