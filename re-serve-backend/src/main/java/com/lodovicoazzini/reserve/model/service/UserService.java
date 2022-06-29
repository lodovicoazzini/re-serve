package com.lodovicoazzini.reserve.model.service;

import com.lodovicoazzini.reserve.model.entity.User;
import com.lodovicoazzini.reserve.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User saveUser(final User user) {
        return userRepository.save(user);
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
