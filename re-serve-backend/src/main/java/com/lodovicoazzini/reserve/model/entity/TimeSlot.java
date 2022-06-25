package com.lodovicoazzini.reserve.model.entity;

import com.lodovicoazzini.reserve.enums.OverlapType;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface TimeSlot extends Comparable<TimeSlot> {

    Timestamp getStartTime();

    Timestamp getEndTime();

    private TimeSlot combine(
            final TimeSlot other,
            final Function<TimeSlot, TimeSlot> noOverlapCallback,
            final BiFunction<TimeSlot, TimeSlot, TimeSlot> overlapCallback,
            final BiFunction<TimeSlot, TimeSlot, TimeSlot> edgeCallback) {
        // Find the slot starting first
        final TimeSlot first;
        final TimeSlot second;
        if (this.compareTo(other) <= 0) {
            first = this;
            second = other;
        } else {
            first = other;
            second = this;
        }
        // Verify the overlap
        if (first.getEndTime().compareTo(second.getStartTime()) < 0) {
            // No overlap
            return noOverlapCallback.apply(this);
        } else if (first.getEndTime().compareTo(second.getStartTime()) == 0) {
            // Edge overlap
            return edgeCallback.apply(first, second);
        } else {
            // Overlap
            return overlapCallback.apply(first, second);
        }
    }

    default <T extends TimeSlot> OverlapType getOverlapType(final T other) {
        final OverlapType[] result = new OverlapType[1];
        this.combine(
                other,
                (original) -> {
                    result[0] = OverlapType.DISTINCT;
                    return null;
                },
                (first, second) -> {
                    result[0] = OverlapType.OVERLAP;
                    return null;
                },
                (first, second) -> {
                    result[0] = OverlapType.EDGE;
                    return null;
                }
        );
        return result[0];
    }

    default <T extends TimeSlot> T merge(final T other, final BiFunction<Timestamp, Timestamp, T> generator) {
        final BiFunction<TimeSlot, TimeSlot, TimeSlot> mergeLogic = (first, second) -> {
            // Find the overall end time (the second might be included in the first)
            final Timestamp endTime = first.getEndTime().compareTo(second.getEndTime()) > 0
                    ? first.getEndTime()
                    : second.getEndTime();
            // Return the slot
            return generator.apply(first.getStartTime(), endTime);
        };
        final TimeSlot merged = this.combine(
                other,
                (original) -> original,
                mergeLogic,
                mergeLogic
        );
        return generator.apply(merged.getStartTime(), merged.getEndTime());
    }

    default <T extends TimeSlot> T merge(final List<T> others, final BiFunction<Timestamp, Timestamp, T> generator) {
        // Combine the slots
        return others.stream()
                .sorted()
                .reduce(
                        generator.apply(this.getStartTime(), this.getEndTime()),
                        (acc, other) -> acc.merge(other, generator)
                );
    }

    default <T extends TimeSlot> Optional<T> getOverlap(
            final T other,
            final BiFunction<Timestamp, Timestamp, T> generator) {
        final Optional<TimeSlot> overlap = Optional.ofNullable(this.combine(
                other,
                (original) -> null,
                (first, second) -> {
                    // Find the sooner end time (the second might be included in the first)
                    final Timestamp endTime = first.getEndTime().compareTo(second.getEndTime()) > 0
                            ? second.getEndTime()
                            : first.getEndTime();
                    // Return the slot
                    return generator.apply(second.getStartTime(), endTime);
                },
                (first, second) -> null
        ));
        if (overlap.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(generator.apply(overlap.get().getStartTime(), overlap.get().getEndTime()));
        }
    }

    default <T extends TimeSlot> Optional<T> getOverlap(
            final List<T> others,
            final BiFunction<Timestamp, Timestamp, T> generator) {
        // Find the overlap, otherwise null
        return others.stream()
                .sorted()
                .filter(other -> this.getOverlap(other, generator).isPresent())
                .findFirst();
    }

    default <Other extends TimeSlot, Result extends TimeSlot> Optional<Result> subtract(
            final Other other,
            final BiFunction<Timestamp, Timestamp, Result> generator) {
        final Optional<TimeSlot> subtracted = Optional.ofNullable(this.combine(
                other,
                (original) -> original,
                (first, second) -> {
                    if (first.equals(second)) {
                        // Same duration -> deleted
                        return null;
                    } else if (this.equals(first)) {
                        // This comes first -> keep until the second starts
                        return generator.apply(this.getStartTime(), second.getStartTime());
                    } else {
                        if (this.getEndTime().compareTo(first.getEndTime()) <= 0) {
                            // The current slot is included in the other one -> deleted
                            return null;
                        } else {
                            // The other comes first -> keep from the end of the other
                            return generator.apply(first.getEndTime(), this.getEndTime());
                        }
                    }
                },
                (first, second) -> this
        ));
        if (subtracted.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(generator.apply(subtracted.get().getStartTime(), subtracted.get().getEndTime()));
        }
    }

    default <T extends TimeSlot> Optional<T> subtract(
            final List<T> others,
            final BiFunction<Timestamp, Timestamp, T> generator) {
        // Combine the slots
        return Optional.ofNullable(others.stream()
                .sorted()
                .reduce(
                        generator.apply(this.getStartTime(), this.getEndTime()),
                        (acc, other) -> acc.subtract(other, generator).orElse(null)
                ));
    }

    @Override
    default int compareTo(TimeSlot other) {
        return this.getStartTime().compareTo(other.getStartTime());
    }
}
