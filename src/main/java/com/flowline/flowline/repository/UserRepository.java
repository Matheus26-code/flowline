package com.flowline.flowline.repository;

import com.flowline.flowline.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
