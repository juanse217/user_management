package com.sebastian.dev.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sebastian.dev.usermanagement.model.document.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void deleteByUsername(String username);

    void deleteByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
