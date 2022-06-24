package com.lodovicoazzini.reserve.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public ResponseEntity<String> createAvailability(
            @PathVariable("startTime") final String startTime,
            @PathVariable("endTime") final String endTime
    ) {
        final Availability saved = availabilityService.saveAvailability(new Availability(
                Timestamp.valueOf(startTime),
                Timestamp.valueOf(endTime)
        ));
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final String encoded = mapper.writeValueAsString(saved);
            return new ResponseEntity<>(encoded, HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
