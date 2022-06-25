package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Optional;

import static com.lodovicoazzini.reserve.utils.ControllerUtils.encodeResponse;

@RestController
@RequestMapping("/reserve/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("create/{startTime}/{endTime}/{title}/{email}")
    public ResponseEntity<String> createReservation(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("title") final String title,
            @PathVariable("email") final String email) {
        final Reservation reservation;
        try {
            reservation = new Reservation(
                    Timestamp.valueOf(startTime),
                    Timestamp.valueOf(endTime),
                    title,
                    email);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            final Optional<Reservation> saved = reservationService.saveReservation(reservation);
            if (saved.isEmpty()) {
                // No available slot -> notify frontend
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (reservation.equals(saved.get())) {
                // No merge
                return encodeResponse(saved, HttpStatus.CREATED);
            } else {
                // Merge
                return encodeResponse(saved, HttpStatus.OK);
            }
        } catch (PSQLException e) {
            // Duplicate
            return encodeResponse(reservation, HttpStatus.CONTINUE);
        }
    }
}
