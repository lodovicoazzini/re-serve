package com.lodovicoazzini.reserve.model.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "availability")
@Table(name = "availability")
public class Availability {

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
}