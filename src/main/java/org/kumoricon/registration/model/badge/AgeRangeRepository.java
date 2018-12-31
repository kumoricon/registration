package org.kumoricon.registration.model.badge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgeRangeRepository extends JpaRepository<AgeRange, Integer> {
    List<AgeRange> findByNameStartsWithIgnoreCase(String name);
}