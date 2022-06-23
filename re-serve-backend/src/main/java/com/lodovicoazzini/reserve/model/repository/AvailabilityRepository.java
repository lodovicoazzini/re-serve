package com.lodovicoazzini.reserve.model.repository;

import com.lodovicoazzini.reserve.model.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
