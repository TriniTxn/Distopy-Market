package com.distopy.repository;

import com.distopy.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDetails, Integer> {


}
