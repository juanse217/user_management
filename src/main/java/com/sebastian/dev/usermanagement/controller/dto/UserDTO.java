package com.sebastian.dev.usermanagement.controller.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sebastian.dev.usermanagement.shared.Configuration;
import com.sebastian.dev.usermanagement.shared.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

public record UserDTO(

        @NotNull(message = "The id must be valid") @Size(min = 1) 
        String id,
        @NotBlank(message = "The username must be valid") 
        String username,
        @Email(message = "The email must be valid") 
        String email,
        @NotBlank(groups = OnCreate.class, message = "The password must be valid") @Size(groups = OnCreate.class, min = 8, message = "The password must have at least 8 characters") @JsonProperty( access = JsonProperty.Access.WRITE_ONLY)
        String password,
        @NotNull(message = "The roles must be valid") @Size(min = 1, message = "You have to add at least 1 role") 
        Set<Role> roles,
        Configuration configuration // Optional.
) {
    public interface OnCreate extends Default {
    }
}
