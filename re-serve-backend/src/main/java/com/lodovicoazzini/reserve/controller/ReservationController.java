package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static com.lodovicoazzini.reserve.utils.ControllerUtils.encodeResponse;
import static java.lang.Long.parseLong;

@RestController
@RequestMapping("/reserve/reservation")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("create/{startTime}/{endTime}/{title}/{reservedBy}/{reservedFrom}")
    public ResponseEntity<String> createReservation(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("title") final String title,
            @PathVariable("reservedBy") final String reservedBy,
            @PathVariable("reservedFrom") final String reservedFrom
    ) {
        final Reservation reservation;
        System.out.println("request received");
        try {
            reservation = new Reservation(
                    new Timestamp(parseLong(startTime)),
                    new Timestamp(parseLong(endTime)),
                    title,
                    new User(reservedBy),
                    new User(reservedFrom)
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        final Optional<Reservation> saved = reservationService.saveReservation(reservation);
        if (saved.isEmpty()) {
            // No available slot -> notify frontend
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (reservation.equals(saved.get())) {
            // No merge
            return encodeResponse(saved.get(), HttpStatus.CREATED);
        } else {
            // Merge
            return encodeResponse(saved.get(), HttpStatus.OK);
        }
    }

    @GetMapping("remove/{startTime}/{endTime}/{reservedBy}")
    public ResponseEntity<String> removeReservation(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("reservedBy") final String reservedBy) {
        final Reservation reservation;
        try {
            reservation = new Reservation();
            reservation.setStartTime(new Timestamp(parseLong(startTime)));
            reservation.setEndTime(new Timestamp(parseLong(endTime)));
            reservation.setReservedBy(new User(reservedBy));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // Find a reservation that matches the provided one
        final Optional<Reservation> match = reservationService.findReservationsLike(reservation).stream().findFirst();
        if (match.isEmpty()) {
            // No matching reservation -> notify frontend
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
