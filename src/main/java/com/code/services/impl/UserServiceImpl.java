package com.code.services.impl;

import org.springframework.stereotype.Service;

import com.code.entities.User;
import com.code.repositories.UserRepository;
import com.code.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        log.info("Creating user: {}", user);
        User newUser = userRepository.save(user);
        log.info("User created with id: {} in database : {}", newUser.getId(), (newUser.getId() % 2 + 1));

        return newUser;
    }



}
