package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lodovicoazzini.reserve.utils.ControllerUtils.encodeResponse;

@RestController
@RequestMapping("/reserve/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class UserController {

    private final UserService userService;

    @GetMapping("create/{email}")
    public ResponseEntity<String> createAvailability(@PathVariable("email") final String email) {
        final User user = new User(email);
        final Optional<User> saved = userService.saveUser(user);
        if (saved.isPresent()) {
            return encodeResponse(saved, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("listAvailabilities/{email}")
    public ResponseEntity<String> listAvailabilities(
            @PathVariable("email") final String email
    ) {
        final Optional<User> user = userService.findUsersLike(new User(email)).stream().findFirst();
        if (user.isEmpty()) {
            return encodeResponse(new ArrayList<>(), HttpStatus.OK);
        } else {
            final List<Availability> availabilities = user.get().getAvailabilities();
            return encodeResponse(user.get().getAvailabilities(), HttpStatus.OK);
        }
    }

    @GetMapping("listReservations/{email}")
    public ResponseEntity<String> listReservations(
            @PathVariable("email") final String email
    ) {
        final Optional<User> user = userService.findUsersLike(new User(email)).stream().findFirst();
        if (user.isEmpty()) {
            return encodeResponse(new ArrayList<>(), HttpStatus.OK);
        } else {
            return encodeResponse(user.get().getReservations(), HttpStatus.OK);
        }
    }

    @GetMapping("listCommitments/{email}")
    public ResponseEntity<String> listReservationsByUser(
            @PathVariable("email") final String email
    ) {
        final Optional<User> user = userService.findUsersLike(new User(email)).stream().findFirst();
        if (user.isEmpty()) {
            return encodeResponse(new ArrayList<>(), HttpStatus.OK);
        } else {
            final List<Reservation> reservations = user.get().getCommitments();
            return encodeResponse(reservations, HttpStatus.OK);
        }
    }

    @GetMapping("listCommitments/{email}/{bookedBy}")
    public ResponseEntity<String> listReservationsByUser(
            @PathVariable("email") final String email,
            @PathVariable("bookedBy") final String bookedBy
    ) {
        final Optional<User> user = userService.findUsersLike(new User(email)).stream().findFirst();
        if (user.isEmpty()) {
            return encodeResponse(new ArrayList<>(), HttpStatus.OK);
        } else {
            final List<Reservation> reservations = user.get().getCommitments().stream()
                    .filter(reservation -> reservation.getReservedBy().getEmail().equals(bookedBy))
                    .collect(Collectors.toList());
            return encodeResponse(reservations, HttpStatus.OK);
        }
    }
}
