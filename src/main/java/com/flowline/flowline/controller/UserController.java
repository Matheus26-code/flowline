package com.flowline.flowline.controller;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.UserRequestDTO;
import com.flowline.flowline.dto.UserResponseDTO;
import com.flowline.flowline.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGE')")
    public ResponseEntity<UserResponseDTO> create (
            @RequestBody @Valid UserRequestDTO request) {
        UserResponseDTO result = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGE')")
    public ResponseEntity<UserResponseDTO> getUserById (@PathVariable long id) {
        UserResponseDTO result = userService.findUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGE')")
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> getAllUsers(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponseDTO<UserResponseDTO> result = userService.findAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGE')")
    public ResponseEntity<UserResponseDTO> updateUser (
            @PathVariable long id, @Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO result = userService.updateUser(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGE')")
    public ResponseEntity<Void> deleteUser (@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}