package com.lodovicoazzini.reserve.model.entity;

import javax.persistence.*;
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
