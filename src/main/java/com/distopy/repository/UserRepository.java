package com.distopy.repository;

import com.distopy.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

    UserDtls findByEmail(String email);

    List<UserDtls> findByRole(String role);

    UserDtls findByResetToken(String resetToken);
}
