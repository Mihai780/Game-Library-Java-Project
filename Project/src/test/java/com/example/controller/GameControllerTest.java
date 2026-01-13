package com.example.controller;

import com.example.project.controller.GameController;
import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.service.GameService;
import com.example.project.service.GameTagService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private GameTagService gameTagService;

    @InjectMocks
    private GameController gameController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Game game;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(gameController)
                .setValidator(validator)
                .build();

        game = new Game();
        game.setId(1L);
        game.setName("Elden Ring");
    }

    @Test
    void createGame_Success() throws Exception {
        when(gameService.create(any(Game.class))).thenReturn(game);

        mockMvc.perform(post("/rest/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(game)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Elden Ring"));

        verify(gameService).create(any(Game.class));
    }

    @Test
    void createGame_DuplicateName_ReturnsConflict() throws Exception {
        when(gameService.create(any(Game.class)))
                .thenThrow(new ConflictException("Game with this name already exists."));

        mockMvc.perform(post("/rest/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(game)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConflictException));

        verify(gameService).create(any(Game.class));
    }

    @Test
    void getAllGames_Success() throws Exception {
        Game game2 = new Game();
        game2.setId(2L);
        game2.setName("Hades");

        List<Game> games = Arrays.asList(game, game2);
        when(gameService.getAll()).thenReturn(games);

        mockMvc.perform(get("/rest/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Elden Ring"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Hades"));

        verify(gameService).getAll();
    }

    @Test
    void getGameById_NotFound_Returns404() throws Exception {
        when(gameService.getById(999L)).thenThrow(new NotFoundException("Game not found."));

        mockMvc.perform(get("/rest/games/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        verify(gameService).getById(999L);
    }

    @Test
    void deleteGame_Success() throws Exception {
        doNothing().when(gameService).delete(1L);

        mockMvc.perform(delete("/rest/games/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(gameService).delete(1L);
    }

    @Test
    void searchByName_Success() throws Exception {
        when(gameService.searchByName("eld")).thenReturn(Arrays.asList(game));

        mockMvc.perform(get("/rest/games/search/name").param("name", "eld"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Elden Ring"));

        verify(gameService).searchByName("eld");
    }

    @Test
    void addTagToGame_Success() throws Exception {
        when(gameService.addTag(1L, 2L)).thenReturn(game);

        mockMvc.perform(post("/rest/games/{gameId}/tags/{tagId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Elden Ring"));

        verify(gameService).addTag(1L, 2L);
    }

    @Test
    void removeTagFromGame_Success() throws Exception {
        when(gameService.removeTag(1L, 2L)).thenReturn(game);

        mockMvc.perform(delete("/rest/games/{gameId}/tags/{tagId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Elden Ring"));

        verify(gameService).removeTag(1L, 2L);
    }
}
