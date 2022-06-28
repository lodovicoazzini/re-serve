package com.lodovicoazzini.reserve.model.repository;

import com.lodovicoazzini.reserve.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
