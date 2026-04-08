package com.sebastian.dev.usermanagement.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sebastian.dev.usermanagement.exception.UserAlreadyExistsException;
import com.sebastian.dev.usermanagement.exception.UserNotFoundException;
import com.sebastian.dev.usermanagement.model.document.User;
import com.sebastian.dev.usermanagement.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository repo; 

    public UserService(UserRepository repo){
        this.repo = repo;
    }

    public User createUser(User user){
        if(user == null){
            throw new IllegalArgumentException("The user must be valid");
        }

        try {
            return repo.save(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // The atomic write failed due to a unique index collision. 
            // It is now safe to perform sequential checks to determine exactly which field caused the collision
            // so we can return a precise error message to the client.
            if(user.getId() != null && repo.existsById(user.getId())) {
                throw new UserAlreadyExistsException("The user with id " + user.getId() + " already exists");
            }
            if(repo.existsByUsername(user.getUsername())) {
                throw new UserAlreadyExistsException("The user with username " + user.getUsername() + " already exists");
            }
            if(repo.existsByEmail(user.getEmail())) {
                throw new UserAlreadyExistsException("The user with email " + user.getEmail() + " already exists");
            }
            // Fallback in case it's another unique index
            throw new UserAlreadyExistsException("A user with identical unique credentials already exists.");
        }
    }

    public List<User> findAllUsersPaged(int size, int page, String sortDir, String property){
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, property.toLowerCase());

        Pageable pageable = PageRequest.of(page, size, sort);
        
        return repo.findAll(pageable).getContent();
    }

    public User findUserById(String id){
        if(id == null || id.isBlank()){
            throw new IllegalArgumentException("The id must be valid");
        }

        return repo.findById(id).orElseThrow(() -> new UserNotFoundException("The user with id " + id + " not found"));
    }

    public User findUserByUsername(String username){
        return repo.findByUsername(username).orElseThrow(() -> new UserNotFoundException("The user with username " + username + " not found"));
    }

    public User findUserByEmail(String email){
        return repo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("The user with email " + email + " not found"));
    }

    public void deleteUserById(String id){
        repo.deleteById(id);
    }

    public void deleteUserByUsername(String username){
        repo.deleteByUsername(username);
    }

    public void deleteUserByEmail(String email){
        repo.deleteByEmail(email);
    }

}
