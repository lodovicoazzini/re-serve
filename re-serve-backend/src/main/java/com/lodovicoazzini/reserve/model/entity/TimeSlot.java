package com.lodovicoazzini.reserve.model.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TimeSlot extends Comparable<TimeSlot> {

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

    TimeSlot merge(final TimeSlot other);

    TimeSlot merge(final List<TimeSlot> others);

    TimeSlot getOverlap(final TimeSlot other);

    TimeSlot getOverlap(final List<TimeSlot> others);

    Timestamp getStartTime();

    Timestamp getEndTime();
}
