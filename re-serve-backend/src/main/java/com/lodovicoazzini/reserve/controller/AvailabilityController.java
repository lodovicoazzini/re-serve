package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.service.AvailabilityService;
import com.lodovicoazzini.reserve.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

import static com.lodovicoazzini.reserve.utils.ControllerUtils.encodeResponse;
import static java.lang.Long.parseLong;

@RestController
@RequestMapping("/reserve/availability")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final UserService userService;

    @GetMapping("create/{startTime}/{endTime}/{owner}")
    public ResponseEntity<String> createAvailability(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("owner") final String owner
    ) {
        final Availability availability;
        final User matchedOwner = userService.listUsersLike(new User(owner)).stream().findFirst().orElse(new User(owner));
        try {
            availability = new Availability(
                    new Timestamp(parseLong(startTime)),
                    new Timestamp(parseLong(endTime)),
                    matchedOwner
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        final Availability saved = availabilityService.saveAvailability(availability);
        if (availability.equals(saved)) {
            // No merge
            return encodeResponse(saved, HttpStatus.CREATED);
        } else {
            // Merge
            return encodeResponse(saved, HttpStatus.OK);
        }
    }

    @GetMapping("remove/{startTime}/{endTime}/{owner}")
    public ResponseEntity<Integer> removeAvailability(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("owner") final String owner
    ) {
        final Availability availability = new Availability(
                new Timestamp(parseLong(startTime)),
                new Timestamp(parseLong(endTime)),
                new User(owner)
        );
        final List<Availability> similar = availabilityService.findAvailabilitiesLike(availability);
        similar.forEach(availabilityService::deleteAvailability);
        return new ResponseEntity<>(similar.size(), HttpStatus.OK);
    }

    @GetMapping("subtract/{startTime}/{endTime}/{owner}")
    public ResponseEntity<Integer> subtractAvailability(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime,
            @PathVariable("owner") final String owner
    ) {
        final Availability availability = new Availability(
                Timestamp.valueOf(startTime),
                Timestamp.valueOf(endTime),
                new User(owner)
        );
        // Subtract the availabilities and count the affected entities
        int affectedCount = availabilityService.subtractAvailability(availability);
        return new ResponseEntity<>(affectedCount, HttpStatus.OK);
    }

    @GetMapping("list")
    public ResponseEntity<String> listAvailabilities() {
        final List<Availability> availabilities = availabilityService.listAvailabilities();
        return encodeResponse(availabilities, HttpStatus.OK);
    }

    @GetMapping("list/{owner}")
    public ResponseEntity<String> listOwnerAvailabilities(@PathVariable("owner") final String owner) {
        final Availability template = new Availability();
        template.setOwner(new User(owner));
        final List<Availability> availabilities = availabilityService.findAvailabilitiesLike(template);
        return encodeResponse(availabilities, HttpStatus.OK);
    }
}
