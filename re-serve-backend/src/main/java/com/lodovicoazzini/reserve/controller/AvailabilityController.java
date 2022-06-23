package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping("/reserve/availability")
@RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    @GetMapping("create/{startTime}/{endTime}")
    public ResponseEntity<HttpStatus> createAvailability(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime
    ) {
        System.out.printf("start: %s end: %s%n", startTime, endTime);
        final Availability availability = availabilityService.saveAvailability(new Availability(
                Timestamp.valueOf(startTime),
                Timestamp.valueOf(endTime)
        ));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
