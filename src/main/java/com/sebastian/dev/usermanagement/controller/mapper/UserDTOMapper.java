package com.sebastian.dev.usermanagement.controller.mapper;

import org.springframework.stereotype.Component;

import com.sebastian.dev.usermanagement.controller.dto.UserDTO;
import com.sebastian.dev.usermanagement.model.document.User;

@Component
public class UserDTOMapper {
    
    public UserDTO toUserDTO(User u){
        return new UserDTO(u.getId(), u.getUsername(), u.getEmail(), null, u.getRoles(), u.getConfiguration());
    }

    public User toUserEntity(UserDTO dto){
        User user = new User();
        user.setId(dto.id());
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRoles(dto.roles());
        user.setConfiguration(dto.configuration());
        return user;
    }
}
