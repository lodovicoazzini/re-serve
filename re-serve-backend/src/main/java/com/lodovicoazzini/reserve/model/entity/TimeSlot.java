package com.lodovicoazzini.reserve.model.entity;

import java.sql.Timestamp;
import java.util.List;

public interface TimeSlot extends Comparable<TimeSlot> {

    TimeSlot merge(final TimeSlot other);

    TimeSlot merge(final List<TimeSlot> others);

    TimeSlot getOverlap(final TimeSlot other);

    TimeSlot getOverlap(final List<TimeSlot> others);

    Timestamp getStartTime();

    Timestamp getEndTime();
}
