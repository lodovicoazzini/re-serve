package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.enums.OverlapType;
import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AvailabilityService availabilityService;

    public Optional<Reservation> saveReservation(final Reservation reservation) {
        final Optional<Reservation> saved;
        // Verify that the reservation is included in an available slot
        final List<Availability> availabilities = availabilityService.listAvailabilities();
        final Optional<Availability> match = availabilities.stream()
                .filter(availability -> availability.getOverlapType(reservation) != OverlapType.DISTINCT)
                .findFirst();
        if (match.isPresent()) {
            // Included -> merge the reservation with the ones with the same email and title
            final List<Reservation> reservations = this.listReservations().stream()
                    .filter(other -> other.getTitle().equals(reservation.getTitle())
                            && other.getEmail().equals(reservation.getEmail()))
                    .collect(Collectors.toList());
            final Reservation merged = reservation.merge(
                    reservation,
                    reservation::cloneWithSlot
            );
            // Remove the previous reservations and update the availabilities
            reservations.stream()
                    .filter(other -> merged.getOverlapType(other) != OverlapType.DISTINCT)
                    .forEach(this::deleteReservation);
            saved = Optional.of(reservationRepository.save(reservation));
            // Update the availability
            availabilityService.deleteAvailability(match.get());
            final Optional<Availability> updated = match.get().subtract(reservation, Availability::new);
            // Not fully consumed -> save
            updated.ifPresent(availabilityService::saveAvailability);
        } else {
            // Not included -> return empty
            saved = Optional.empty();
        }
        return saved;
    }

    public Availability deleteReservation(final Reservation reservation) {
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
