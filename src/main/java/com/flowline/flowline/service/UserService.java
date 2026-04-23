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
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
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
        UserDependecies deps = resolveDependecies(request);

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setWarehouse(deps.warehouse);
        return toResponse(userRepository.save(user));
    }

    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toResponse(user);
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
        UserDependecies deps = resolveDependecies(request);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setWarehouse(deps.warehouse);
        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}