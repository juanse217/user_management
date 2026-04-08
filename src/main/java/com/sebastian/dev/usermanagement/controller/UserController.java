package com.sebastian.dev.usermanagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sebastian.dev.usermanagement.controller.dto.UserDTO;
import com.sebastian.dev.usermanagement.controller.dto.UserDTO.OnCreate;
import com.sebastian.dev.usermanagement.controller.mapper.UserDTOMapper;
import com.sebastian.dev.usermanagement.model.document.User;
import com.sebastian.dev.usermanagement.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Email;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserDTOMapper mapper;

    public UserController(UserService service, UserDTOMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Returns a list containing all the users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "found all created users", 
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = UserDTO.class))),
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> findAllUsersPaged(@RequestParam(defaultValue = "1") int size,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "id") String sortingProperty) {
        return ResponseEntity.ok(
                service.findAllUsersPaged(size, page, sort, sortingProperty).stream().map(mapper::toUserDTO).toList());
    }

    @Operation(summary = "Finds a user with the specified username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the user with specified username"),
        @ApiResponse(responseCode = "404", description = "User with specified username not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> findUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(mapper.toUserDTO(service.findUserByUsername(username)));
    }

    @Operation(summary = "Finds a user with the specified id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the user with specified id"),
        @ApiResponse(responseCode = "404", description = "User with specified id not found")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toUserDTO(service.findUserById(id)));
    }

    @Operation(summary = "Finds a user with the specified email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the user with specified email"),
        @ApiResponse(responseCode = "404", description = "User with specified email not found"),
        @ApiResponse(responseCode = "400", description = "Email not valid")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> findUserByEmail(@Email @PathVariable String email) {
        return ResponseEntity.ok(mapper.toUserDTO(service.findUserByEmail(email)));
    }

    @Operation(summary = "Creates a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User was created"),
        @ApiResponse(responseCode = "409", description = "User with email/username/id already exists")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createNewUser(@Validated(OnCreate.class) @RequestBody UserDTO dto) {
        User userEntity = mapper.toUserEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body((mapper.toUserDTO(service.createUser(userEntity))));
    }

    @Operation(summary = "Deletes a user by id")
    @ApiResponse(responseCode = "204", description = "The user was deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id) {
        service.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletes a user by username")
    @ApiResponse(responseCode = "204", description = "The user was deleted")
    @DeleteMapping("/deletion/username")
    public ResponseEntity<Void> deleteUserByEmailOrUsername(@RequestParam(required = true) String username) {
        service.deleteUserByUsername(username);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletes a user by email")
    @ApiResponse(responseCode = "204", description = "The user was deleted")
    @DeleteMapping("/deletion/email")
    public ResponseEntity<Void> deleteByEmail(@RequestParam(required = true) String email) {
        service.deleteUserByEmail(email);

        return ResponseEntity.noContent().build();
    }
}
