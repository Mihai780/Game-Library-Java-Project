package com.example.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.GameTag;
import com.example.project.repository.GameRepository;
import com.example.project.repository.GameTagRepository;
import com.example.project.repository.PurchaseRepository;
import com.example.project.repository.ReviewRepository;
import com.example.project.service.GameService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameTagRepository gameTagRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private GameTag tag;

    @BeforeEach
    void setUp() {
        game = new Game();
        game.setName("Elden Ring");

        tag = new GameTag();
        tag.setName("RPG");
    }

    @Test
    void createGame_Success() {
        when(gameRepository.findByName("Elden Ring")).thenReturn(Optional.empty());
        when(gameRepository.save(game)).thenReturn(game);

        Game result = gameService.create(game);

        assertNotNull(result);
        assertEquals("Elden Ring", result.getName());
        verify(gameRepository).findByName("Elden Ring");
        verify(gameRepository).save(game);
    }

    @Test
    void createGame_DuplicateName_ThrowsConflict() {
        when(gameRepository.findByName("Elden Ring")).thenReturn(Optional.of(new Game()));

        ConflictException ex = assertThrows(ConflictException.class, () -> gameService.create(game));
        assertEquals("Game with this name already exists.", ex.getMessage());

        verify(gameRepository).findByName("Elden Ring");
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void getAll_Success() {
        when(gameRepository.findAll()).thenReturn(List.of(game));

        List<Game> result = gameService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findAll();
    }

    @Test
    void getById_Success() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        Game result = gameService.getById(1L);

        assertNotNull(result);
        verify(gameRepository).findById(1L);
    }

    @Test
    void getById_NotFound_Throws() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> gameService.getById(999L));
        assertEquals("Game not found.", ex.getMessage());

        verify(gameRepository).findById(999L);
    }

    @Test
    void searchByName_Success() {
        when(gameRepository.findByNameContainingIgnoreCase("elden")).thenReturn(List.of(game));

        List<Game> result = gameService.searchByName("elden");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findByNameContainingIgnoreCase("elden");
    }

    @Test
    void searchByTags_NullOrEmpty_ReturnsEmpty() {
        assertTrue(gameService.searchByTags(null).isEmpty());
        assertTrue(gameService.searchByTags(List.of()).isEmpty());

        verifyNoInteractions(gameRepository);
    }

    @Test
    void searchByTags_NormalizesAndCallsRepo() {
        when(gameRepository.findByAnyTagNames(List.of("rpg", "action"))).thenReturn(List.of(game));

        List<Game> result = gameService.searchByTags(List.of(" RPG ", "Action"));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findByAnyTagNames(List.of("rpg", "action"));
    }

    @Test
    void delete_Success() {
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(purchaseRepository.existsByGameId(1L)).thenReturn(false);
        when(reviewRepository.existsByGameId(1L)).thenReturn(false);

        gameService.delete(1L);

        verify(gameRepository).existsById(1L);
        verify(purchaseRepository).existsByGameId(1L);
        verify(reviewRepository).existsByGameId(1L);
        verify(gameRepository).deleteById(1L);
    }

    @Test
    void delete_NotFound_Throws() {
        when(gameRepository.existsById(999L)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> gameService.delete(999L));
        assertEquals("Game not found.", ex.getMessage());

        verify(gameRepository).existsById(999L);
        verify(gameRepository, never()).deleteById(anyLong());
    }

    @Test
    void addTag_Success() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameTagRepository.findById(2L)).thenReturn(Optional.of(tag));

        Game result = gameService.addTag(1L, 2L);

        assertNotNull(result);
        assertTrue(result.getTags().contains(tag));

        verify(gameRepository).findById(1L);
        verify(gameTagRepository).findById(2L);
    }

    @Test
    void addTag_TagNotFound_Throws() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameTagRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> gameService.addTag(1L, 2L));
        assertEquals("Tag not found.", ex.getMessage());

        verify(gameRepository).findById(1L);
        verify(gameTagRepository).findById(2L);
    }

    @Test
    void removeTag_Success() {
        game.addTag(tag);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameTagRepository.findById(2L)).thenReturn(Optional.of(tag));

        Game result = gameService.removeTag(1L, 2L);

        assertNotNull(result);
        assertFalse(result.getTags().contains(tag));

        verify(gameRepository).findById(1L);
        verify(gameTagRepository).findById(2L);
    }

    @Test
    void removeTag_TagNotFound_Throws() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameTagRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> gameService.removeTag(1L, 2L));
        assertEquals("Tag not found.", ex.getMessage());

        verify(gameRepository).findById(1L);
        verify(gameTagRepository).findById(2L);
    }
}
