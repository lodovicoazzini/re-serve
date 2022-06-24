package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    public Availability saveAvailability(final Availability availability) {
        final Availability savedAvailability;
        // Get the list of the existing availabilities
        List<Availability> overlapping = availabilityRepository.findAll().stream()
                .filter(other -> availability.merge(other) != null)
                .collect(Collectors.toList());
        if (overlapping.isEmpty()) {
            // No overlap -> save the availability
            savedAvailability = availabilityRepository.save(availability);
        } else {
            // Overlap -> merge the availabilities
            // Remove all the old availabilities
            availabilityRepository.deleteAll(overlapping);
            // Add the availability to the list of overlapping
            overlapping.add(availability);
            // Sort the availabilities and find the overall start and end time
            overlapping = overlapping.stream().sorted().collect(Collectors.toList());
            final Availability overlap = new Availability(
                    overlapping.get(0).getStartTime(),
                    overlapping.get(overlapping.size() - 1).getEndTime()
            );
            // Save the overlap
            savedAvailability = availabilityRepository.save(overlap);
        }
        return savedAvailability;
    }

    public void deleteAvailability(final Availability availability) {
        availabilityRepository.delete(availability);
    }

    public List<Availability> findAll() {
        return availabilityRepository.findAll();
    }
}
