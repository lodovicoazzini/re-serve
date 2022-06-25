package com.lodovicoazzini.reserve.model.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity(name = "reservation")
@Table(
        name = "reservation",
        uniqueConstraints = {
                @UniqueConstraint(name = "reservation_slot_unique", columnNames = {"start_time", "end_time"})})
public class Reservation implements TimeSlot {

    @Id
    @SequenceGenerator(name = "reservation_sequence", sequenceName = "reservation_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "end_time", nullable = false)
    private Timestamp endTime;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "email", nullable = false, columnDefinition = "TEXT")
    private String email;

    public Reservation() {
    }

    /**
     * Reservation constructor
     *
     * @param startTime The start time of the availability slot
     * @param endTime   The end time of the availability slot
     * @throws IllegalArgumentException If the slot duration is not positive
     */
    public Reservation(Timestamp startTime, Timestamp endTime, String title, String email) throws IllegalArgumentException {
        if (endTime.compareTo(startTime) <= 0) {
            throw new IllegalArgumentException("The time interval must have a positive duration");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.email = email;
    }

    public Reservation cloneWithSlot(final Timestamp startTime, final Timestamp endTime) {
        return new Reservation(startTime, endTime, this.title, this.email);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Reservation{start=%s, end=%s, title='%s', email='%s'}".formatted(startTime, endTime, title, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return startTime.equals(that.startTime) && endTime.equals(that.endTime) && title.equals(that.title) && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, title, email);
    }
}
