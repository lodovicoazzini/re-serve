package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.model.entity.Availability;
import com.lodovicoazzini.reserve.model.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    public Availability saveAvailability(final Availability availability) throws PSQLException {
        // Get the list of the existing availabilities
        List<Availability> availabilities = availabilityRepository.findAll();
        final Availability merged = availability.merge(
                availabilities,
                Availability::new
        );
        // Remove the previous availabilities
        availabilities.stream()
                .filter(other -> availability.getOverlap(other, Availability::new).isPresent())
                .forEach(entity -> {
                    System.out.println(entity);
                    availabilityRepository.delete(entity);
                });
        // Save the merged availability
        return availabilityRepository.save(merged);
    }

    public void deleteAvailability(final Availability availability) {
        availabilityRepository.delete(availability);
    }

    public List<Availability> findAll() {
        return availabilityRepository.findAll();
    }

    public List<Availability> findLike(final Availability availability) {
        return availabilityRepository.findAll(Example.of(availability));
    }
}
