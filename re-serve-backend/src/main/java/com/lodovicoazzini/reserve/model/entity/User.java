package com.lodovicoazzini.reserve.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "reserve_user")
@Table(
        name = "reserve_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "reserve_user_email_unique", columnNames = {"email"})})
public class User {

    @Id
    @SequenceGenerator(name = "reserve_user_sequence", sequenceName = "reserve_user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reserve_user_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, columnDefinition = "TEXT")
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Availability> availabilities;

    @JsonIgnore
    @OneToMany(mappedBy = "reservedBy", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "reservedFrom", cascade = CascadeType.ALL)
    private List<Reservation> commitments;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Availability> getAvailabilities() {
        return availabilities != null ? availabilities : new ArrayList<>();
    }

    public void addAvailability(final Availability availability) {
        if (this.availabilities == null) {
            this.availabilities = new ArrayList<>();
        }
        this.availabilities.add(availability);
        availability.setOwner(this);
    }

    public List<Reservation> getCommitments() {
        return commitments != null ? commitments : new ArrayList<>();
    }

    public List<Reservation> getReservations() {
        return reservations != null ? reservations : new ArrayList<>();
    }

    public void addReservation(final Reservation reservation) {
        if (this.reservations == null) {
            this.reservations = new ArrayList<>();
        }
        this.reservations.add(reservation);
        reservation.setReservedBy(this);
    }

    public void addCommitment(final Reservation reservation) {
        if (this.commitments == null) {
            this.commitments = new ArrayList<>();
        }
        this.commitments.add(reservation);
        reservation.setReservedFrom(this);
    }

    @Override
    public String toString() {
        return "User{email='%s'}".formatted(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User reserve_user = (User) o;
        return email.equals(reserve_user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
