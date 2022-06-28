package com.lodovicoazzini.reserve.model.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity(name = "availability")
@Table(
        name = "availability",
        uniqueConstraints = {
                @UniqueConstraint(name = "availability_slot_unique", columnNames = {"start_time", "end_time"})})
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


    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Availability() {
    }

    /**
     * Availability constructor
     *
     * @param startTime The start time of the availability slot
     * @param endTime   The end time of the availability slot
     * @throws IllegalArgumentException If the slot duration is not positive
     */
    public Availability(Timestamp startTime, Timestamp endTime, User owner) throws IllegalArgumentException {
        if (endTime.compareTo(startTime) <= 0) {
            throw new IllegalArgumentException("The time interval must have a positive duration");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.owner = owner;
    }

    public Availability cloneWithSlot(final Timestamp startTime, final Timestamp endTime) {
        return new Availability(startTime, endTime, this.owner);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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

    public void setStartTime(Timestamp startTime) throws IllegalArgumentException {
        if (this.endTime != null && startTime.compareTo(this.endTime) >= 0) {
            throw new IllegalArgumentException("The time interval must have a positive duration");
        }
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) throws IllegalArgumentException {
        if (this.startTime != null && endTime.compareTo(this.startTime) <= 0) {
            throw new IllegalArgumentException("The time interval must have a positive duration");
        }
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Availability{startTime=%s, endTime=%s, owner=%s}".formatted(startTime, endTime, owner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Availability that = (Availability) o;
        return startTime.equals(that.startTime) && endTime.equals(that.endTime) && owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, owner);
    }
}
