package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.data.domain.Example;
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
        final List<Availability> availabilities = availabilityService.listAvailabilities();
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

    public Availability deleteReservation(final Reservation reservation) throws PSQLException {
        // Delete the reservation
        reservationRepository.delete(reservation);
        // Restore the reserved slot as an availability
        final Availability restored = new Availability(reservation.getStartTime(), reservation.getEndTime());
        return availabilityService.saveAvailability(restored);
    }

    public List<Reservation> listReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> findReservationsLike(final Reservation reservation) {
        return reservationRepository.findAll(Example.of(reservation));
    }
}
