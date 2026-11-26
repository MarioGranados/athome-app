package com.athome.application.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.athome.application.model.User;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

    User findAllByUsername(String username);


    Optional<User> findByUsername(String username);
    
}
