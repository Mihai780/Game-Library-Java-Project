package com.example.controller;

import com.example.project.controller.UserController;
import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.User;
import com.example.project.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setValidator(validator)
                .build();

        user = new User();
        user.setId(1L);
        user.setUsername("mihai");
        user.setBalanceCents(0L);
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/rest/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("mihai"));

        verify(userService).create(any(User.class));
    }

    @Test
    void createUser_DuplicateUsername_ReturnsConflict() throws Exception {
        when(userService.create(any(User.class)))
                .thenThrow(new ConflictException("Username already exists."));

        mockMvc.perform(post("/rest/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConflictException));

        verify(userService).create(any(User.class));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("alex");

        List<User> users = Arrays.asList(user, user2);
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/rest/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(userService).getAll();
    }

    @Test
    void getUserById_NotFound_Returns404() throws Exception {
        when(userService.getById(999L)).thenThrow(new NotFoundException("User not found."));

        mockMvc.perform(get("/rest/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        verify(userService).getById(999L);
    }

    @Test
    void getOwnedGames_Success() throws Exception {
        Game game = new Game();
        game.setId(10L);
        game.setName("Hades");

        Set<Game> owned = new HashSet<>();
        owned.add(game);

        when(userService.getOwnedGames(1L)).thenReturn(owned);

        mockMvc.perform(get("/rest/users/{userId}/owned-games", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hades"));

        verify(userService).getOwnedGames(1L);
    }

    @Test
    void addOwnedGame_Success() throws Exception {
        when(userService.addOwnedGame(1L, 10L)).thenReturn(user);

        mockMvc.perform(post("/rest/users/{userId}/owned-games/{gameId}", 1L, 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("mihai"));

        verify(userService).addOwnedGame(1L, 10L);
    }

    @Test
    void removeOwnedGame_Success() throws Exception {
        when(userService.removeOwnedGame(1L, 10L)).thenReturn(user);

        mockMvc.perform(delete("/rest/users/{userId}/owned-games/{gameId}", 1L, 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("mihai"));

        verify(userService).removeOwnedGame(1L, 10L);
    }
}
