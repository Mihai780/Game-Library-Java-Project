package com.example.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.GameTag;
import com.example.project.repository.GameTagRepository;
import com.example.project.service.GameTagService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameTagServiceTest {

    @Mock
    private GameTagRepository gameTagRepository;

    @InjectMocks
    private GameTagService gameTagService;

    private GameTag tag;

    @BeforeEach
    void setUp() {
        tag = new GameTag();
        tag.setName("RPG");
    }

    @Test
    void createTag_Success() {
        when(gameTagRepository.findByNameIgnoreCase("RPG")).thenReturn(Optional.empty());
        when(gameTagRepository.save(any(GameTag.class))).thenReturn(tag);

        GameTag result = gameTagService.create(tag);

        assertNotNull(result);
        assertEquals("RPG", result.getName());
        verify(gameTagRepository).findByNameIgnoreCase("RPG");
        verify(gameTagRepository).save(tag);
    }

    @Test
    void createTag_Duplicate_ThrowsConflict() {
        when(gameTagRepository.findByNameIgnoreCase("RPG")).thenReturn(Optional.of(new GameTag()));

        ConflictException ex = assertThrows(ConflictException.class, () -> gameTagService.create(tag));
        assertEquals("Tag with this name already exists.", ex.getMessage());

        verify(gameTagRepository).findByNameIgnoreCase("RPG");
        verify(gameTagRepository, never()).save(any(GameTag.class));
    }

    @Test
    void getAll_Success() {
        when(gameTagRepository.findAll()).thenReturn(List.of(tag));

        List<GameTag> result = gameTagService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameTagRepository).findAll();
    }

    @Test
    void getById_Success() {
        when(gameTagRepository.findById(1L)).thenReturn(Optional.of(tag));

        GameTag result = gameTagService.getById(1L);

        assertNotNull(result);
        verify(gameTagRepository).findById(1L);
    }

    @Test
    void getById_NotFound_Throws() {
        when(gameTagRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> gameTagService.getById(999L));
        assertEquals("Tag not found.", ex.getMessage());

        verify(gameTagRepository).findById(999L);
    }

    @Test
    void delete_Success() {
        when(gameTagRepository.existsById(1L)).thenReturn(true);

        gameTagService.delete(1L);

        verify(gameTagRepository).existsById(1L);
        verify(gameTagRepository).deleteById(1L);
    }

    @Test
    void delete_NotFound_Throws() {
        when(gameTagRepository.existsById(999L)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> gameTagService.delete(999L));
        assertEquals("Tag not found.", ex.getMessage());

        verify(gameTagRepository).existsById(999L);
        verify(gameTagRepository, never()).deleteById(anyLong());
    }
}
