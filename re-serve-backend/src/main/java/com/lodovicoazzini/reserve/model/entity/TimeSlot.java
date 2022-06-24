package com.lodovicoazzini.reserve.model.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface TimeSlot extends Comparable<TimeSlot> {

    Timestamp getStartTime();

    Timestamp getEndTime();

    default TimeSlot combine(
            final TimeSlot other,
            final Function<TimeSlot, TimeSlot> failureCallback,
            final BiFunction<TimeSlot, TimeSlot, TimeSlot> successCallback) {
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
            return failureCallback.apply(this);
        } else {
            return successCallback.apply(first, second);
        }
    }

    default <T extends TimeSlot> T merge(final T other, final BiFunction<Timestamp, Timestamp, T> generator) {
        final TimeSlot merged = this.combine(
                other,
                (original) -> original,
                (first, second) -> {
                    // Find the overall end time (the second might be included in the first)
                    final Timestamp endTime = first.getEndTime().compareTo(second.getEndTime()) > 0
                            ? first.getEndTime()
                            : second.getEndTime();
                    // Return the slot
                    return generator.apply(first.getStartTime(), endTime);
                }
        );
        return generator.apply(merged.getStartTime(), merged.getEndTime());
    }

    default <T extends TimeSlot> T merge(final List<T> others, final BiFunction<Timestamp, Timestamp, T> generator) {
        // Combine the slots
        final TimeSlot merged = others.stream()
                .sorted()
                .reduce(
                        generator.apply(this.getStartTime(), this.getEndTime()),
                        (acc, other) -> acc.merge(other, generator)
                );
        return generator.apply(merged.getStartTime(), merged.getEndTime());
    }

    default TimeSlot getOverlap(final TimeSlot other, final BiFunction<Timestamp, Timestamp, TimeSlot> generator) {
        return this.combine(
                other,
                (original) -> null,
                (first, second) -> {
                    // Find the sooner end time (the second might be included in the first)
                    final Timestamp endTime = first.getEndTime().compareTo(second.getEndTime()) > 0
                            ? second.getEndTime()
                            : first.getEndTime();
                    // Return the slot
                    return generator.apply(second.getStartTime(), endTime);
                }
        );
    }

    default TimeSlot getOverlap(final List<TimeSlot> others, final BiFunction<Timestamp, Timestamp, TimeSlot> generator) {
        // Find the overlap, otherwise null
        return others.stream()
                .sorted()
                .filter(other -> this.getOverlap(other, generator) != null)
                .findFirst().orElse(null);
    }

    @Override
    default int compareTo(TimeSlot other) {
        return this.getStartTime().compareTo(other.getStartTime());
    }
}
