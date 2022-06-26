package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.lodovicoazzini.reserve.utils.ControllerUtils.encodeResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTest {

    @InjectMocks
    private AvailabilityController availabilityController;

    @Mock
    private AvailabilityService availabilityService;

    private Availability mockAvailability;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        mockAvailability = new Availability(
                Timestamp.valueOf("2022-06-25 09:00:00"),
                Timestamp.valueOf("2022-06-25 11:00:00")
        );
    }

    @Test
    void testCreateAvailabilityNew() {
        final String startTime = "2022-06-25 09:00:00";
        final String endTime = "2022-06-25 11:00:00";
        when(this.availabilityService.saveAvailability(any(Availability.class))).thenReturn(mockAvailability);
        final ResponseEntity<String> response = this.availabilityController.createAvailability(startTime, endTime);
        assertEquals(encodeResponse(mockAvailability, HttpStatus.CREATED), response);
    }

    @Test
    void testCreateAvailabilityInvalidTimeString() {
        final String startTime = "2022-06-2509:00:00";
        final String endTime = "2022-06-25 11:00:00";
        final ResponseEntity<String> response = availabilityController.createAvailability(startTime, endTime);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateAvailabilityZeroDuration() {
        final String startTime = "2022-06-25 09:00:00";
        final String endTime = "2022-06-25 09:00:00";
        final ResponseEntity<String> response = availabilityController.createAvailability(startTime, endTime);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateAvailabilityNegativeDuration() {
        final String startTime = "2022-06-25 09:00:00";
        final String endTime = "2022-06-25 08:00:00";
        final ResponseEntity<String> response = availabilityController.createAvailability(startTime, endTime);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateAvailabilityMerge() {
        final String startTime = "2022-06-25 10:00:00";
        final String endTime = "2022-06-25 12:00:00";
        final Availability overlapping = new Availability(
                Timestamp.valueOf("2022-06-25 09:00:00"),
                Timestamp.valueOf("2022-06-25 11:00:00")
        );
        when(availabilityService.saveAvailability(any(Availability.class))).thenReturn(overlapping);
        final ResponseEntity<String> response = availabilityController.createAvailability(
                startTime,
                endTime
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateAvailabilityDuplicate() {
        final String startTime = "2022-06-25 10:00:00";
        final String endTime = "2022-06-25 12:00:00";
        final Availability duplicate = new Availability(
                Timestamp.valueOf(startTime),
                Timestamp.valueOf(endTime)
        );
        when(availabilityService.saveAvailability(any(Availability.class))).thenReturn(duplicate);
        final ResponseEntity<String> response = availabilityController.createAvailability(
                startTime,
                endTime
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testRemoveAvailabilityExisting() {
        when(availabilityService.findAvailabilitiesLike(any(Availability.class))).thenReturn(List.of(mockAvailability));
        doNothing().when(availabilityService).deleteAvailability(any(Availability.class));
        final ResponseEntity<Integer> response = availabilityController.removeAvailability(
                "2022-06-25 09:00:00",
                "2022-06-25 11:00:00"
        );
        assertEquals(new ResponseEntity<>(1, HttpStatus.OK), response);
    }

    @Test
    void testRemoveAvailabilityNonExisting() {
        when(availabilityService.findAvailabilitiesLike(any(Availability.class))).thenReturn(new ArrayList<>());
        final ResponseEntity<Integer> response = availabilityController.removeAvailability(
                "2022-06-25 09:00:00",
                "2022-06-25 11:00:00"
        );
        assertEquals(new ResponseEntity<>(0, HttpStatus.OK), response);
    }

    @Test
    void testSubtractAvailability() {
        when(availabilityService.subtractAvailability(any(Availability.class))).thenReturn(1);
        final ResponseEntity<Integer> response = availabilityController.subtractAvailability(
                "2022-06-25 09:00:00",
                "2022-06-25 10:00:00"
        );
        assertEquals(new ResponseEntity<>(1, HttpStatus.OK), response);
    }

    @Test
    void testListAvailabilities() {
        when(availabilityService.listAvailabilities()).thenReturn(List.of(mockAvailability));
        final ResponseEntity<String> response = availabilityController.listAvailabilities();
        assertEquals(encodeResponse(List.of(mockAvailability), HttpStatus.OK), response);
    }

    @Test
    void testListAvailabilitiesEmpty() {
        when(availabilityService.listAvailabilities()).thenReturn(new ArrayList<>());
        final ResponseEntity<String> response = availabilityController.listAvailabilities();
        assertEquals(encodeResponse(new ArrayList<>(), HttpStatus.OK), response);
    }
}