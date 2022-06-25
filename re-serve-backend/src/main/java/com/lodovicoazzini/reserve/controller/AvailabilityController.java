package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

import static com.lodovicoazzini.reserve.utils.ControllerUtils.encodeResponse;

@RestController
@RequestMapping("/reserve/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("create/{startTime}/{endTime}")
    public ResponseEntity<String> createAvailability(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime) {
        final Availability availability;
        try {
            availability = new Availability(Timestamp.valueOf(startTime), Timestamp.valueOf(endTime));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            final Availability saved = availabilityService.saveAvailability(availability);
            if (availability.equals(saved)) {
                // No merge
                return encodeResponse(saved, HttpStatus.CREATED);
            } else {
                // Merge
                return encodeResponse(saved, HttpStatus.OK);
            }
        } catch (PSQLException e) {
            // Duplicate
            return encodeResponse(availability, HttpStatus.CONTINUE);
        }
    }

    @GetMapping("remove/{startTime}/{endTime}")
    public ResponseEntity<Integer> removeAvailability(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime) {
        final Availability availability = new Availability(Timestamp.valueOf(startTime), Timestamp.valueOf(endTime));
        final List<Availability> similar = availabilityService.findAvailabilitiesLike(availability);
        similar.forEach(availabilityService::deleteAvailability);
        return new ResponseEntity<>(similar.size(), HttpStatus.OK);
    }

    @GetMapping("subtract/{startTime}/{endTime}")
    public ResponseEntity<Long> subtractAvailability(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime) {
        final Availability availability = new Availability(Timestamp.valueOf(startTime), Timestamp.valueOf(endTime));
        // Subtract the availabilities and count the affected entities
        long affectedCount = availabilityService.subtractAvailability(availability);
        return new ResponseEntity<>(affectedCount, HttpStatus.OK);
    }

    @GetMapping("list")
    public ResponseEntity<String> listAvailabilities() {
        final List<Availability> availabilities = availabilityService.listAvailabilities();
        return encodeResponse(availabilities, HttpStatus.OK);
    }
}
