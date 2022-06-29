package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> saveUser(final User user) {
        try {
            return Optional.of(userRepository.save(user));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public void deleteUser(final User user) {
        userRepository.delete(user);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public List<User> findUsersLike(final User user) {
        return userRepository.findAll(Example.of(user));
    }
}
