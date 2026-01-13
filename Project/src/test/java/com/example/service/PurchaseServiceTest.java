package com.example.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.Purchase;
import com.example.project.model.User;
import com.example.project.repository.GameRepository;
import com.example.project.repository.PurchaseRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.PurchaseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    private User user;
    private Game game;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("mihai");
        user.increaseBalance(5000L);

        game = new Game();
        game.setName("Hades");
    }

    @Test
    void createPurchase_Success() {
        when(purchaseRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        Purchase result = purchaseService.create(1L, 2L, 1999L);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(game, result.getGame());
        assertEquals(1999L, result.getPriceCents());

        // economic coherence
        assertEquals(3001L, user.getBalanceCents());
        assertTrue(user.getOwnedGames().contains(game));

        ArgumentCaptor<Purchase> captor = ArgumentCaptor.forClass(Purchase.class);
        verify(purchaseRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(game, captor.getValue().getGame());
        assertEquals(1999L, captor.getValue().getPriceCents());
    }

    @Test
    void createPurchase_InsufficientBalance_ThrowsConflict() {
        User poorUser = new User();
        poorUser.setUsername("poor");

        when(purchaseRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(poorUser));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> purchaseService.create(1L, 2L, 1999L));

        assertEquals("Insufficient balance", ex.getMessage());
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    @Test
    void createPurchase_Duplicate_ThrowsConflict() {
        when(purchaseRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> purchaseService.create(1L, 2L, 1999L));

        assertEquals("User already purchased this game", ex.getMessage());
        verify(purchaseRepository).existsByUserIdAndGameId(1L, 2L);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    @Test
    void createPurchase_UserNotFound_Throws() {
        when(purchaseRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> purchaseService.create(1L, 2L, 1999L));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    @Test
    void createPurchase_GameNotFound_Throws() {
        when(purchaseRepository.existsByUserIdAndGameId(1L, 2L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> purchaseService.create(1L, 2L, 1999L));

        assertEquals("Game not found", ex.getMessage());
        verify(gameRepository).findById(2L);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    @Test
    void getByUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(purchaseRepository.findByUserId(1L)).thenReturn(List.of(new Purchase()));

        List<Purchase> result = purchaseService.getByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).existsById(1L);
        verify(purchaseRepository).findByUserId(1L);
    }

    @Test
    void getByUser_UserNotFound_Throws() {
        when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> purchaseService.getByUser(1L));
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).existsById(1L);
        verify(purchaseRepository, never()).findByUserId(anyLong());
    }
}
