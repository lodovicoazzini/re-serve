package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.enums.OverlapType;
import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.entity.Reservation;
import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    public List<Availability> saveAvailability(final Availability availability) {
        // Get the owner of the availability
        final User owner = availability.getOwner();
        // Get the list of the existing overlapping availabilities
        final List<Availability> overlappingAvailabilities = owner.getAvailabilities().stream()
                .filter(other -> other.getOverlapType(availability) != OverlapType.DISTINCT)
                .collect(Collectors.toList());
        // Merge the availability with the overlapping ones
        final Availability merged = availability.merge(
                overlappingAvailabilities,
                availability::cloneWithSlot
        );
        // Remove the previous availabilities
        overlappingAvailabilities.forEach(this::deleteAvailability);
        // Subtract the overlapping user commitments
        final List<Reservation> overlappingReservations = owner.getCommitments().stream()
                .filter(commitment -> commitment.getOverlapType(merged) == OverlapType.OVERLAP)
                .collect(Collectors.toList());
        final List<Availability> subtracted = merged.subtract(overlappingReservations, merged::cloneWithSlot);
        // Save the resulting availabilities
        try {
            return availabilityRepository.saveAll(subtracted);
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }

    public void deleteAvailability(final Availability availability) {
        availabilityRepository.delete(availability);
    }

    public int subtractAvailability(final Availability availability) {
        // Get the list of the existing availabilities that overlap the given one
        final List<Availability> availabilities = this.listAvailabilities().stream()
                .filter(saved -> saved.getOverlapType(availability) != OverlapType.DISTINCT)
                .collect(Collectors.toList());
        // Subtract the availability from the list and update the persisted value
        availabilities.forEach(saved -> {
            // Delete the saved availability
            this.deleteAvailability(saved);
            // Subtract the availability
            final Optional<Availability> subtracted = saved.subtract(availability, availability::cloneWithSlot);
            // If the availability is not completely deleted -> save the remainder
            subtracted.ifPresent(this::saveAvailability);
        });
        // Return the number of affected availabilities
        return availabilities.size();
    }

    public List<Availability> listAvailabilities() {
        return availabilityRepository.findAll();
    }

    public List<Availability> findAvailabilitiesLike(final Availability availability) {
        return availabilityRepository.findAll(Example.of(availability));
    }
}
