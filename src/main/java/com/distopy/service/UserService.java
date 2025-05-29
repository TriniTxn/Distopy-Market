package com.distopy.service;

import com.distopy.model.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserDtls saveUser(UserDtls user);

    UserDtls getUserByEmail(String email);

    List<UserDtls> getUsers(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);

    void increaseFailedLoginAttempts(UserDtls user);

    void userAccountLock(UserDtls user);

    Boolean unlockAccountTimeExpired(UserDtls user);

    void resetAttempt(int userId);

    void updateUserResetToken(String email, String resetToken);

    UserDtls getUserByResetToken(String resetToken);

    UserDtls updateUser(UserDtls user);

    UserDtls updateUserProfile(UserDtls user, MultipartFile img);
}
