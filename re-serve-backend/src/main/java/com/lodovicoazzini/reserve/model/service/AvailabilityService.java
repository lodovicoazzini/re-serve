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
        // Get the list of the existing availabilities
        List<Availability> availabilities = availabilityRepository.findAll().stream()
                .map(a -> new Availability(a.getStartTime(), a.getEndTime()))
                .collect(Collectors.toList());
        final Availability merged = availability.merge(
                availabilities,
                Availability::new
        );
        return new Availability(merged.getStartTime(), merged.getEndTime());
    }

    public void deleteAvailability(final Availability availability) {
        availabilityRepository.delete(availability);
    }

    public List<Availability> findAll() {
        return availabilityRepository.findAll();
    }
}
