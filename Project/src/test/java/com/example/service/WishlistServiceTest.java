package com.example.service;

import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.User;
import com.example.project.model.Wishlist;
import com.example.project.repository.GameRepository;
import com.example.project.repository.UserRepository;
import com.example.project.repository.WishlistRepository;
import com.example.project.service.WishlistService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private User user;
    private Wishlist wishlist;
    private Game game;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("mihai");

        wishlist = new Wishlist();
        wishlist.setUser(user);

        game = new Game();
        game.setName("Hollow Knight");
    }

    @Test
    void getOrCreateByUserId_UserNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> wishlistService.getOrCreateByUserId(1L));
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).findById(1L);
        verifyNoInteractions(wishlistRepository);
    }

    @Test
    void getOrCreateByUserId_ExistingWishlist_ReturnsIt() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));

        Wishlist result = wishlistService.getOrCreateByUserId(1L);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(wishlistRepository).findByUserId(1L);
        verify(wishlistRepository, never()).save(any(Wishlist.class));
    }

    @Test
    void getOrCreateByUserId_CreatesWishlist_WhenMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(inv -> inv.getArgument(0));

        Wishlist result = wishlistService.getOrCreateByUserId(1L);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(result, user.getWishlist()); // both sides consistent

        verify(userRepository).findById(1L);
        verify(wishlistRepository).findByUserId(1L);
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void getGames_ReturnsGames() {
        wishlist.addGame(game);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));

        Set<Game> result = wishlistService.getGames(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(game));
    }

    @Test
    void addGame_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));

        Wishlist result = wishlistService.addGame(1L, 2L);

        assertNotNull(result);
        assertTrue(result.getGames().contains(game));

        verify(gameRepository).findById(2L);
    }

    @Test
    void addGame_GameNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));
        when(gameRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> wishlistService.addGame(1L, 2L));
        assertEquals("Game not found", ex.getMessage());

        verify(gameRepository).findById(2L);
    }

    @Test
    void removeGame_Success() {
        wishlist.addGame(game);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));

        Wishlist result = wishlistService.removeGame(1L, 2L);

        assertNotNull(result);
        assertFalse(result.getGames().contains(game));

        verify(gameRepository).findById(2L);
    }

    @Test
    void getById_NotFound_Throws() {
        when(wishlistRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> wishlistService.getById(99L));
        assertEquals("Wishlist not found with id: 99", ex.getMessage());

        verify(wishlistRepository).findById(99L);
    }

    @Test
    void delete_Success() {
        when(wishlistRepository.findById(10L)).thenReturn(Optional.of(wishlist));

        wishlistService.delete(10L);

        verify(wishlistRepository).findById(10L);
        verify(wishlistRepository).delete(wishlist);
    }
}
