package com.example.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.User;
import com.example.project.repository.GameRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Game game;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("mihai");

        game = new Game();
        game.setName("Hades");
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByUsername("mihai")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(user);

        assertNotNull(result);
        assertEquals("mihai", result.getUsername());
        verify(userRepository).findByUsername("mihai");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_Duplicate_ThrowsConflict() {
        when(userRepository.findByUsername("mihai")).thenReturn(Optional.of(new User()));

        ConflictException ex = assertThrows(ConflictException.class, () -> userService.create(user));
        assertEquals("Username already exists.", ex.getMessage());

        verify(userRepository).findByUsername("mihai");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAll_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertNotNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getById_NotFound_Throws() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.getById(999L));
        assertEquals("User not found.", ex.getMessage());

        verify(userRepository).findById(999L);
    }

    @Test
    void getByUsername_Success() {
        when(userRepository.findByUsername("mihai")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("mihai");

        assertNotNull(result);
        verify(userRepository).findByUsername("mihai");
    }

    @Test
    void getByUsername_NotFound_Throws() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.getByUsername("missing"));
        assertEquals("User not found.", ex.getMessage());

        verify(userRepository).findByUsername("missing");
    }

    @Test
    void getOwnedGames_Success() {
        user.addOwnedGame(game);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Game> result = userService.getOwnedGames(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findById(1L);
    }

    @Test
    void addOwnedGame_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));

        User result = userService.addOwnedGame(1L, 2L);

        assertNotNull(result);
        assertTrue(result.getOwnedGames().contains(game));

        verify(userRepository).findById(1L);
        verify(gameRepository).findById(2L);
    }

    @Test
    void addOwnedGame_GameNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.addOwnedGame(1L, 2L));
        assertEquals("Game not found.", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(gameRepository).findById(2L);
    }

    @Test
    void removeOwnedGame_Success() {
        user.addOwnedGame(game);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));

        User result = userService.removeOwnedGame(1L, 2L);

        assertNotNull(result);
        assertFalse(result.getOwnedGames().contains(game));

        verify(userRepository).findById(1L);
        verify(gameRepository).findById(2L);
    }
}
