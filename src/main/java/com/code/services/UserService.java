package com.code.services;

import java.util.List;

import com.code.entities.User;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    List<User> getUsersByName(String name);

}
