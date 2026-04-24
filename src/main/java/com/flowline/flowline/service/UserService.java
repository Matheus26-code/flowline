package com.flowline.flowline.service;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.UserResponseDTO;
import com.flowline.flowline.dto.UserRequestDTO;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.User;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.UserRepository;
import com.flowline.flowline.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    private record UserDependecies( Warehouse warehouse ) {}

    private UserDependecies resolveDependecies(UserRequestDTO request) {
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Warehouse not found with id: " + request.warehouseId()));

        return new UserDependecies(warehouse);
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getWarehouse().getId());
    }

    public UserResponseDTO createUser(UserRequestDTO request) {
        log.info("Creating user: {}", request);
        UserDependecies deps = resolveDependecies(request);

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setWarehouse(deps.warehouse);
        UserResponseDTO result = toResponse(userRepository.save(user));
        log.info("User created successfully: id={}, name={}",
                result.id(), result.username());
        return result;
    }

    public UserResponseDTO findUserById(Long id) {
        log.info("Finding user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        UserResponseDTO result = toResponse(user);
        log.info("User find successfully: id={}, name={}",
                result.id(), result.username());
        return result;
    }

    public PageResponseDTO<UserResponseDTO> findAllUsers(Pageable pageable) {
        Page<UserResponseDTO> page = userRepository.findAll(pageable)
                .map(this::toResponse);
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        log.info("Updating user: {}", request);
        UserDependecies deps = resolveDependecies(request);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setWarehouse(deps.warehouse);
        UserResponseDTO result = toResponse(userRepository.save(user));
        log.info("User updated successfully: id={}, name={}",
                result.id(), result.username());
        return result;
    }

    public void deleteUser(Long id) {
        log.info("Deleting user by id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion: id={}", id);
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted: id={}", id);
    }
}