package com.lodovicoazzini.reserve.model.repository;

import com.lodovicoazzini.reserve.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
