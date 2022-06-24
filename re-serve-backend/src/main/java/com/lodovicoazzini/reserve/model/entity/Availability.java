package com.lodovicoazzini.reserve.model.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Entity(name = "availability")
@Table(name = "availability")
public class Availability implements TimeSlot {

    @Id
    @SequenceGenerator(name = "availability_sequence", sequenceName = "availability_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "availability_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "end_time", nullable = false)
    private Timestamp endTime;

    public Availability() {
    }

    public Availability(Timestamp start, Timestamp end) {
        this.startTime = start;
        this.endTime = end;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp start) {
        this.startTime = start;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp end) {
        this.endTime = end;
    }

    @Override
    public String toString() {
        return "Availability{start=%s, end=%s}".formatted(startTime, endTime);
    }

    @Override
    public int compareTo(TimeSlot other) {
        return this.startTime.compareTo(other.getStartTime());
    }

    @Override
    public TimeSlot merge(TimeSlot other) {
        return this.combine(
                other,
                (original) -> original,
                (first, second) -> {
                    // Find the overall end time (the second might be included in the first)
                    final Timestamp endTime = first.getEndTime().compareTo(second.getEndTime()) > 0
                            ? first.getEndTime()
                            : second.getEndTime();
                    // Return the availability corresponding to the slot
                    return new Availability(
                            first.getStartTime(),
                            endTime
                    );
                }
        );
    }

    @Override
    public Availability merge(final List<TimeSlot> others) {
        // Combine the overlapping slots
        final TimeSlot mergedSlot = others.stream()
                .sorted()
                .reduce(
                new Availability(this.startTime, this.endTime),
                (acc, other) -> {
                    final TimeSlot merged = acc.merge(other);
                    return new Availability(merged.getStartTime(), merged.getEndTime());
                }
        );
        // Return the availability corresponding to the slot
        return new Availability(mergedSlot.getStartTime(), mergedSlot.getEndTime());
    }

    @Override
    public TimeSlot getOverlap(TimeSlot other) {
        return this.combine(
                other,
                (original) -> null,
                (first, second) -> {
                    // Find the sooner end time (the second might be included in the first)
                    final Timestamp endTime = first.getEndTime().compareTo(second.getEndTime()) > 0
                            ? second.getEndTime()
                            : first.getEndTime();
                    // Return the availability corresponding to the slot
                    return new Availability(
                            second.getStartTime(),
                            endTime
                    );
                }
        );
    }

    @Override
    public TimeSlot getOverlap(List<TimeSlot> others) {
        // Combine the overlapping slots
        final Optional<TimeSlot> overlapSlot = others.stream()
                .sorted()
                .filter(other -> this.getOverlap(other) != null)
                .findFirst();
        // Return the overlap if present, otherwise null
        return overlapSlot.orElse(null);
    }
}
