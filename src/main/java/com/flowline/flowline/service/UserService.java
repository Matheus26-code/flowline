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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    public UserResponseDTO createUser(UserRequestDTO request) {
        User user = new User();
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + request.warehouseId()));

        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setWarehouse(warehouse);
        User savedUser = userRepository.save(user);
        return new UserResponseDTO(
                savedUser.getUsername(),
                savedUser.getRole());
    }

    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return new UserResponseDTO(user.getUsername(), user.getRole());
    }

    public PageResponseDTO<UserResponseDTO> findAllUsers(Pageable pageable) {
        Page<UserResponseDTO> page = userRepository.findAll(pageable)
                .map(user -> new UserResponseDTO(
                        user.getUsername(), user.getRole()));
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + request.warehouseId()));

        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setWarehouse(warehouse);
        User savedUser = userRepository.save(user);
        return new UserResponseDTO(
                savedUser.getUsername(),
                savedUser.getRole());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}