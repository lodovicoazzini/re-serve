package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.enums.OverlapType;
import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AvailabilityService availabilityService;

    public List<Reservation> saveReservation(final Reservation reservation) {
        final User reservedBy = reservation.getReservedBy();
        final User reservedFrom = reservation.getReservedFrom();
        // Verify that the reservation is included in an available slot
        final List<Availability> overlappingAvailabilities = availabilityService.listAvailabilities().stream()
                .filter(availability -> availability.getOwner().equals(reservedFrom)
                        && availability.getOverlapType(reservation) == OverlapType.OVERLAP)
                .collect(Collectors.toList());
        List<Reservation> processedReservations = overlappingAvailabilities.stream().map(availability -> {
            // Get the overlapping section with the availability
            final Reservation overlapReservation = reservation.getOverlap(availability, reservation::cloneWithSlot).get();
            // Subtract the overlap from the availability and update
            final List<Availability> updatedAvailabilities = availability.subtract(overlapReservation, availability::cloneWithSlot);
            availabilityService.deleteAvailability(availability);
            updatedAvailabilities.forEach(availabilityService::saveAvailability);
            // Find the reservation with the others with the same details
            final List<Reservation> toMergeReservations = this.listReservations().stream()
                    .filter(other -> other.getReservedBy().equals(overlapReservation.getReservedBy())
                            && overlapReservation.getOverlapType(other) != OverlapType.DISTINCT
                            && other.getTitle().equals(overlapReservation.getTitle())
                            && other.getReservedFrom().equals(overlapReservation.getReservedFrom()))
                    .collect(Collectors.toList());
            // Merge with the similar reservations
            final Reservation mergedReservation = overlapReservation.merge(
                    toMergeReservations,
                    overlapReservation::cloneWithSlot
            );
            // Delete the previous reservations
            toMergeReservations.forEach(this::deleteReservation);
            // Subtract the processed reservation from the user availabilities
            availabilityService.listAvailabilities().stream()
                    .filter(myAvailability -> myAvailability.getOwner().equals(reservedBy)
                            && myAvailability.getOverlapType(mergedReservation) == OverlapType.OVERLAP)
                    .forEach(myAvailability -> {
                        final List<Availability> updatedMyAvailabilities = myAvailability.subtract(overlapReservation, myAvailability::cloneWithSlot);
                        availabilityService.deleteAvailability(myAvailability);
                        updatedMyAvailabilities.forEach(availabilityService::saveAvailability);
                    });

            // Return the processed reservation
            return mergedReservation;
        }).collect(Collectors.toList());
        // Save the reservations
        try {
            return reservationRepository.saveAll(processedReservations);
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }

    public List<Availability> deleteReservation(final Reservation reservation) {
        // Delete the reservation
        reservationRepository.delete(reservation);
        // Restore the reserved slot as an availability
        final Availability restored = new Availability(reservation.getStartTime(), reservation.getEndTime(), reservation.getReservedFrom());
        return availabilityService.saveAvailability(restored);
    }

    public List<Reservation> listReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> findReservationsLike(final Reservation reservation) {
        return reservationRepository.findAll(Example.of(reservation));
    }
}
