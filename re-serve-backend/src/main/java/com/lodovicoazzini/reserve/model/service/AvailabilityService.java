package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.enums.OverlapType;
import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    public Availability saveAvailability(final Availability availability) {
        // Get the list of the existing availabilities
        final List<Availability> availabilities = availabilityRepository.findAll();
        final Availability merged = availability.merge(
                availabilities,
                Availability::new
        );
        // Remove the previous availabilities
        availabilities.stream()
                .filter(other -> merged.getOverlapType(other) != OverlapType.DISTINCT)
                .forEach(availabilityRepository::delete);
        // Save the merged availability
        return availabilityRepository.save(merged);
    }

    public void deleteAvailability(final Availability availability) {
        availabilityRepository.delete(availability);
    }

    public int subtractAvailability(final Availability availability) {
        // Get the list of the existing availabilities that overlap the given one
        final List<Availability> availabilities = availabilityRepository.findAll().stream()
                .filter(saved -> saved.getOverlapType(availability) != OverlapType.DISTINCT)
                .collect(Collectors.toList());
        // Subtract the availability from the list and update the persisted value
        availabilities.forEach(saved -> {
                    // Delete the saved availability
                    availabilityRepository.delete(saved);
                    // Subtract the availability
                    final Optional<Availability> subtracted = saved.subtract(availability, Availability::new);
                    // If the availability is not completely deleted -> save the remainder
                    subtracted.ifPresent(availabilityRepository::save);
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
