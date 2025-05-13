package com.distopy.service.impl;

import com.distopy.model.UserDetails;
import com.distopy.repository.UserRepository;
import com.distopy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails saveUser(UserDetails user) {
        return userRepository.save(user);
    }
}
