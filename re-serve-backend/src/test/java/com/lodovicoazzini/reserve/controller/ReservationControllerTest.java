package com.lodovicoazzini.reserve.controller;

import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.service.ReservationService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    private Reservation mockReservation;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        mockReservation = new Reservation(
                Timestamp.valueOf("2022-06-25 09:00:00"),
                Timestamp.valueOf("2022-06-25 11:00:00"),
                "my first reservation",
                new User("my.email@reserve.com"),
                new User("my.email@reserve.com")
        );
    }

    @Test
    void testCreateReservationValid() {
        final String startTime = "2022-06-25 09:00:00";
        final String endTime = "2022-06-25 11:00:00";
        final String title = "my first reservation";
        final String email = "my.email@reserve.com";
        when(this.reservationService.saveReservation(any(Reservation.class))).thenReturn(List.of(mockReservation));
        final ResponseEntity<String> response = reservationController.createReservation(
                startTime,
                endTime,
                title,
                email,
                email
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateReservationNotAvailable() {
        final String startTime = "2022-06-25 09:00:00";
        final String endTime = "2022-06-25 11:00:00";
        final String title = "my first reservation";
        final String email = "my.email@reserve.com";
        when(this.reservationService.saveReservation(any(Reservation.class))).thenReturn(new ArrayList<>());
        final ResponseEntity<String> response = reservationController.createReservation(
                startTime,
                endTime,
                title,
                email,
                email
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateReservationMerge() {
        final String startTime = "2022-06-25 11:00:00";
        final String endTime = "2022-06-25 12:00:00";
        final String title = "my first reservation";
        final String email = "my.email@reserve.com";
        when(this.reservationService.saveReservation(any(Reservation.class))).thenReturn(List.of(mockReservation));
        final ResponseEntity<String> response = reservationController.createReservation(
                startTime,
                endTime,
                title,
                email,
                email
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}