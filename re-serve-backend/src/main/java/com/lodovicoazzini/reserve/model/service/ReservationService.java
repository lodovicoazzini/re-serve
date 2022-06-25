package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AvailabilityService availabilityService;

    public Optional<Reservation> saveReservation(final Reservation reservation) throws PSQLException {
        final Optional<Reservation> saved;
        // Verify that the reservation is included in an available slot
        final List<Availability> availabilities = availabilityService.findAll();
        final Optional<Availability> match = availabilities.stream()
                .filter(availability -> availability.getOverlap(reservation, Availability::new).isPresent())
                .findFirst();
        if (match.isPresent()) {
            // Included -> save the reservation and update the available slot
            saved = Optional.of(reservationRepository.save(reservation));
            availabilityService.deleteAvailability(match.get());
            final Optional<Availability> updated = match.get().subtract(reservation, Availability::new);
            if (updated.isPresent()) {
                // Not fully consumed -> save
                availabilityService.saveAvailability(updated.get());
            }
        } else {
            // Not included -> return empty
            saved = Optional.empty();
        }
        return saved;
    }
}
