package com.distopy.service.impl;

import com.distopy.model.Category;
import com.distopy.model.UserDtls;
import com.distopy.repository.UserRepository;
import com.distopy.service.UserService;
import com.distopy.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @Override
    public UserDtls saveUser(UserDtls user) {
        user.setRole("ROLE_USER");
        user.setIsEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedLoginCount(0);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        UserDtls saveUser = userRepository.save(user);
        return saveUser;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDtls> getUsers(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {

        Optional<UserDtls> findByUser = userRepository.findById(id);

        if (findByUser.isPresent()) {
            UserDtls user = findByUser.get();
            user.setIsEnabled(status);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedLoginAttempts(UserDtls user) {
        int attempt = user.getFailedLoginCount() + 1;
        user.setFailedLoginCount(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(UserDtls user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public Boolean unlockAccountTimeExpired(UserDtls user) {

        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (unlockTime < currentTime) {
            user.setAccountNonLocked(true);
            user.setFailedLoginCount(0);
            user.setLockTime(null);
            userRepository.save(user);

            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int userId) {
        Optional<UserDtls> findByUser = userRepository.findById(userId);
        if (findByUser.isPresent()) {
            UserDtls user = findByUser.get();
            user.setFailedLoginCount(0);
            userRepository.save(user);
        }
    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtls findUserByEmail = userRepository.findByEmail(email);
        findUserByEmail.setResetToken(resetToken);
        userRepository.save(findUserByEmail);
    }

    @Override
    public UserDtls getUserByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }

    @Override
    public UserDtls updateUser(UserDtls user) {
        return userRepository.save(user);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls user, MultipartFile img) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User can not be null or empty.");
        }

        UserDtls dbUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        dbUser.setName(user.getName());
        dbUser.setMobileNumber(user.getMobileNumber());
        dbUser.setAddress(user.getAddress());
        dbUser.setCity(user.getCity());
        dbUser.setState(user.getState());
        dbUser.setPincode(user.getPincode());

        try {
            if (img != null && !img.isEmpty()) {
                String contentType = img.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("File must be an image.");
                }

                String imageName = UUID.randomUUID().toString() + ".jpg";

                Path uploadDir = Paths.get("src/main/resources/static/img/profile_img");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path imagePath = uploadDir.resolve(imageName);

                Files.copy(img.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                dbUser.setProfileImage(imageName);
            }

            return userRepository.save(dbUser);

        } catch (IOException e) {
            throw new RuntimeException("Error while processing the profile image: " + e.getMessage(), e);
        }
    }

    @Override
    public UserDtls saveAdmin(UserDtls user) {
        user.setRole("ROLE_ADMIN");
        user.setIsEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedLoginCount(0);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        UserDtls saveUser = userRepository.save(user);
        return saveUser;
    }

    @Override
    public Boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
