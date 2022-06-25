package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;
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
        final Optional<Reservation> saved = reservationService.saveReservation(reservation);
        if (saved.isEmpty()) {
            // No available slot -> notify frontend
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (reservation.equals(saved.get())) {
            // No merge
            return encodeResponse(saved.get(), HttpStatus.CREATED);
        } else {
            // Merge
            return encodeResponse(saved.get(), HttpStatus.OK);
        }
    }

    @GetMapping("remove/{startTime}/{endTime}/{email}")
    public ResponseEntity<String> removeReservation(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("email") final String email) {
        final Reservation reservation;
        try {
            reservation = new Reservation();
            reservation.setStartTime(Timestamp.valueOf(startTime));
            reservation.setEndTime(Timestamp.valueOf(endTime));
            reservation.setEmail(email);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Find a reservation that matches the provided one
        final Optional<Reservation> match = reservationService.findReservationsLike(reservation).stream().findFirst();
        if (match.isEmpty()) {
            // No matching reservation -> notify frontend
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            // Found matching reservation -> delete the reservation and update the availabilities
            final Availability restored = reservationService.deleteReservation(match.get());
            return encodeResponse(restored, HttpStatus.OK);
        }
    }

    @GetMapping("list")
    public ResponseEntity<String> listReservations() {
        final List<Reservation> reservations = reservationService.listReservations();
        return encodeResponse(reservations, HttpStatus.OK);
    }
}
