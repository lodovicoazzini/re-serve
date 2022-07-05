package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.service.ReservationService;
import com.lodovicoazzini.reserve.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lodovicoazzini.reserve.utils.ControllerUtils.encodeResponse;
import static java.lang.Long.parseLong;

@RestController
@RequestMapping("/reserve/reservation")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    @GetMapping("create/{startTime}/{endTime}/{title}/{reservedBy}/{reservedFrom}")
    public ResponseEntity<String> createReservation(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("title") final String title,
            @PathVariable("reservedBy") final String reservedBy,
            @PathVariable("reservedFrom") final String reservedFrom
    ) {
        final Optional<User> matchedReservedBy = userService.findUsersLike(new User(reservedBy)).stream().findFirst();
        final User matchedReservedFrom = userService.findUsersLike(new User(reservedFrom)).stream().findFirst()
                .orElse(new User(reservedFrom));
        if (matchedReservedBy.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Reservation reservation;
        try {
            reservation = new Reservation(
                    new Timestamp(parseLong(startTime)),
                    new Timestamp(parseLong(endTime)),
                    title,
                    matchedReservedBy.get(),
                    matchedReservedFrom
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Please select a valid time interval", HttpStatus.UNAUTHORIZED);
        }
        final List<Reservation> saved = reservationService.saveReservation(reservation);
        if (saved.isEmpty()) {
            // No available slot -> notify frontend
            return new ResponseEntity<>("Please select a time interval corresponding to a reservation", HttpStatus.UNAUTHORIZED);
        } else {
            return encodeResponse(saved, HttpStatus.CREATED);
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
            return new ResponseEntity<>("Please select a valid time interval", HttpStatus.UNAUTHORIZED);
        }
        // Find a reservation that matches the provided one
        final Optional<Reservation> match = reservationService.findReservationsLike(reservation).stream().findFirst();
        if (match.isEmpty()) {
            // No matching reservation -> notify frontend
            return new ResponseEntity<>("Reservation not found", HttpStatus.UNAUTHORIZED);
        } else {
            // Found matching reservation -> delete the reservation and update the availabilities
            final List<Availability> restored = reservationService.deleteReservation(match.get());
            return encodeResponse(restored, HttpStatus.OK);
        }
    }

    @GetMapping("updateTitle/{startTime}/{endTime}/{title}/{reservedBy}/{reservedFrom}")
    public ResponseEntity<String> updateTitle(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("title") final String title,
            @PathVariable("reservedBy") final String reservedBy,
            @PathVariable("reservedFrom") final String reservedFrom
    ) {
        final Optional<User> matchedReservedBy = userService.findUsersLike(new User(reservedBy)).stream().findFirst();
        final User matchedReservedFrom = userService.findUsersLike(new User(reservedFrom)).stream().findFirst()
                .orElse(new User(reservedFrom));
        if (matchedReservedBy.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Find the similar reservations
        final Reservation template = new Reservation();
        template.setStartTime(new Timestamp(parseLong(startTime)));
        template.setEndTime(new Timestamp(parseLong(endTime)));
        template.setReservedBy(matchedReservedBy.get());
        template.setReservedFrom(matchedReservedFrom);
        final List<Reservation> matchReservations = reservationService.findReservationsLike(template);
        // Delete the original reservations
        matchReservations.forEach(reservationService::deleteReservation);
        // Change the titles and update
        matchReservations.forEach(reservation -> reservation.setTitle(title));
        final List<Reservation> saved = matchReservations.stream()
                .map(reservationService::saveReservation)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return encodeResponse(saved, HttpStatus.OK);
    }

    @GetMapping("list")
    public ResponseEntity<String> listReservations() {
        final List<Reservation> reservations = reservationService.listReservations();
        return encodeResponse(reservations, HttpStatus.OK);
    }
}
