package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.lodovicoazzini.reserve.utils.ControllerUtils.encodeResponse;

@RestController
@RequestMapping("/reserve/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class UserController {

    private final UserService userService;

    @GetMapping("create/{email}")
    public ResponseEntity<String> createAvailability(@PathVariable("email") final String email) {
        final User newUser = new User(email);
        if (userService.listUsersLike(newUser).stream().findFirst().isEmpty()) {
            return encodeResponse(newUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("listAvailabilities/{email}")
    public ResponseEntity<String> listAvailabilities(
            @PathVariable("email") final String email
    ) {
        final Optional<User> user = userService.listUsersLike(new User(email)).stream().findFirst();
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
        final Optional<User> user = userService.listUsersLike(new User(email)).stream().findFirst();
        if (user.isEmpty()) {
            return encodeResponse(new ArrayList<>(), HttpStatus.OK);
        } else {
            return encodeResponse(user.get().getReservations(), HttpStatus.OK);
        }
    }
}
