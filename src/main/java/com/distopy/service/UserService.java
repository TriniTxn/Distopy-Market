package com.distopy.service;

import com.distopy.model.UserDtls;

public interface UserService {

    public UserDtls saveUser(UserDtls user);

    public UserDtls getUserByEmail(String email);
}
