package com.lodovicoazzini.reserve.model.entity;

import com.lodovicoazzini.reserve.enums.OverlapType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface TimeSlot extends Comparable<TimeSlot> {

    Timestamp getStartTime();

    Timestamp getEndTime();

    /**
     * @param other             The slot to combine with
     * @param noOverlapCallback The method to build the new slot in case of no overlap
     * @param overlapCallback   The method to build the new slot in case of overlap
     * @param edgeCallback      The method to build the new slot in case of subsequent slots
     * @return A new slot combining the two
     */
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

    /**
     * Get the type of overlap with another slot
     * The type of overlap is
     * DISTINCT if the two slots have no common span
     * OVERLAP if the two slots have some span in common
     * EDGE if the two slots touch each other
     *
     * @param other      The other slot
     * @param <ThisType> The type of the other slot, must extend TimeSlot
     * @return The OverlapType enumeration value corresponding to the type of overlap
     */
    default <ThisType extends TimeSlot> OverlapType getOverlapType(final ThisType other) {
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

    /**
     * Merge with another slot
     * Two slots are merged also if they touch each other
     * If the two slots don't overlap, the original slot is returned
     *
     * @param other      The other slot to merge with
     * @param generator  The method to generate the merged slot
     * @param <ThisType> The type of the slots being merged, must extend TimeSlot
     * @return The merged time slot
     */
    default <ThisType extends TimeSlot> ThisType merge(
            final ThisType other,
            final BiFunction<Timestamp, Timestamp, ThisType> generator
    ) {
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

    /**
     * Merge with a list of other slots
     * Merging the slot with all the others sorted
     *
     * @param others     The list of other slots to merge
     * @param generator  The method to generate the merged slot
     * @param <ThisType> The type of the slots, must extend TimeSlot
     * @return The slot merged with all the others
     */
    default <ThisType extends TimeSlot> ThisType merge(
            final List<ThisType> others,
            final BiFunction<Timestamp, Timestamp, ThisType> generator
    ) {
        // Combine the slots
        return others.stream()
                .sorted()
                .reduce(
                        generator.apply(this.getStartTime(), this.getEndTime()),
                        (acc, other) -> acc.merge(other, generator)
                );
    }

    /**
     * Get the overlapping range of time with another slot
     * If two slots touch each other, there is no overlap
     *
     * @param other      The other slot
     * @param generator  The method to generate the overlapping slot
     * @param <ThisType> The type of the slots, must extend TimeSlot
     * @return The overlapping slot if present, an empty optional otherwise
     */
    default <ThisType extends TimeSlot> Optional<ThisType> getOverlap(
            final ThisType other,
            final BiFunction<Timestamp, Timestamp, ThisType> generator) {
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

    /**
     * Subtract another slot from the current one
     * The type of the slot being subtracted can be different
     *
     * @param other       The slot to subtract
     * @param generator   The method to generate the remaining slot
     * @param <OtherType> The type of the slot to subtract
     * @param <ThisType>  The type of the current slot, and the returned one
     * @return The list of remaining slots (the other can split so can be 2)
     */
    default <OtherType extends TimeSlot, ThisType extends TimeSlot> List<ThisType> subtract(
            final OtherType other,
            final BiFunction<Timestamp, Timestamp, ThisType> generator
    ) {
        final List<ThisType> result = new ArrayList<>();
        this.combine(
                other,
                (original) -> original,
                (first, second) -> {
                    if (first.equals(second)) {
                        // Same duration -> deleted
                    } else if (this.equals(first)) {
                        // This starts first -> keep until the start of the second
                        result.add(generator.apply(this.getStartTime(), second.getStartTime()));
                        if (this.getEndTime().compareTo(second.getEndTime()) > 0) {
                            // This ends after the second -> keep from the end of the second to the end of this
                            result.add(generator.apply(second.getEndTime(), this.getEndTime()));
                        }
                    } else {
                        if (this.getEndTime().compareTo(first.getEndTime()) <= 0) {
                            // This is included in the other -> deleted
                        } else {
                            // The other comes first -> keep from the end of the other to the end of this
                            result.add(generator.apply(first.getEndTime(), this.getEndTime()));
                        }
                    }
                    return null;
                },
                (first, second) -> this
        );
        return result;
    }

    /**
     * Subtract with a list of other slots
     * Subtracting the slot with all the others sorted
     *
     * @param others     The list of other slots to subtract
     * @param generator  The method to generate the subtracted slot
     * @param <ThisType> The type of the slots, must extend TimeSlot
     * @return The slot subtracted with all the others
     */
    default <ThisType extends TimeSlot, OtherType extends TimeSlot> List<ThisType> subtract(
            final List<OtherType> others,
            final BiFunction<Timestamp, Timestamp, ThisType> generator
    ) {
        List<ThisType> result = new ArrayList<>();
        for (OtherType other : others) {
            if (result.isEmpty()) {
                // First iteration -> subtract from the original
                result.addAll(this.subtract(other, generator));
            } else {
                // Subtract from the previous results
                result = result.stream().map(previews -> previews.subtract(other, generator))
                        .flatMap(Collection::stream).collect(Collectors.toList());
            }
        }
        return result;
    }

    @Override
    default int compareTo(TimeSlot other) {
        return this.getStartTime().compareTo(other.getStartTime());
    }
}
