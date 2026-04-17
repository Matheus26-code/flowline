package com.flowline.flowline.service;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.UserRequestDTO;
import com.flowline.flowline.dto.UserResponseDTO;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.User;
import com.flowline.flowline.model.UserRole;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.UserRepository;
import com.flowline.flowline.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private Warehouse warehouse;
    private User user;
    private UserRequestDTO userRequestDTO;


    @Mock
    UserRepository userRepository;
    @Mock
    WarehouseRepository warehouseRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @BeforeEach
    public void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("test");

        user = new User();
        user.setId(1L);
        user.setUsername("User mock");
        user.setEmail("email mock");
        user.setPassword("Password mock");
        user.setRole(UserRole.ADMIN);
        user.setWarehouse(warehouse);

        userRequestDTO = new UserRequestDTO(
                "User mock", "email mock", "Password mock",
                UserRole.ADMIN, warehouse.getId()
        );
    }

    @Test
    public void mustCreateSuccessfullyUser() {
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded_password");
        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.createUser(userRequestDTO);

        assertNotNull(result);
        assertEquals("User mock", result.username());
    }

    @Test
    public void mustFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals("User mock", result.username());
    }

    @Test
    public void mustReturnErrorWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserById(1L)
        );
    }

    @Test
    public void mustFindAllUsers() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        PageResponseDTO<UserResponseDTO> result = userService.findAllUsers(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    public void mustSuccessfullyUpdateUser() {

        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded_password");
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.updateUser(1L, userRequestDTO);

        assertNotNull(result);
        assertEquals("User mock", result.username());
    }

    @Test
    public void mustDeleteUser() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}
