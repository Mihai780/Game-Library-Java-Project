package com.example.controller;

import com.example.project.controller.GameTagController;
import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.GameTag;
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
class GameTagControllerTest {

    @Mock
    private GameTagService tagService;

    @InjectMocks
    private GameTagController tagController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private GameTag tag;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(tagController)
                .setValidator(validator)
                .build();

        tag = new GameTag();
        tag.setId(1L);
        tag.setName("RPG");
    }

    @Test
    void createTag_Success() throws Exception {
        when(tagService.create(any(GameTag.class))).thenReturn(tag);

        mockMvc.perform(post("/rest/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("RPG"));

        verify(tagService).create(any(GameTag.class));
    }

    @Test
    void createTag_Duplicate_ReturnsConflict() throws Exception {
        when(tagService.create(any(GameTag.class)))
                .thenThrow(new ConflictException("Tag with this name already exists."));

        mockMvc.perform(post("/rest/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConflictException));

        verify(tagService).create(any(GameTag.class));
    }

    @Test
    void getAllTags_Success() throws Exception {
        GameTag tag2 = new GameTag();
        tag2.setId(2L);
        tag2.setName("Indie");

        List<GameTag> tags = Arrays.asList(tag, tag2);
        when(tagService.getAll()).thenReturn(tags);

        mockMvc.perform(get("/rest/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(tagService).getAll();
    }

    @Test
    void getTagById_NotFound_Returns404() throws Exception {
        when(tagService.getById(999L)).thenThrow(new NotFoundException("Tag not found."));

        mockMvc.perform(get("/rest/tags/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        verify(tagService).getById(999L);
    }

    @Test
    void deleteTag_Success() throws Exception {
        doNothing().when(tagService).delete(1L);

        mockMvc.perform(delete("/rest/tags/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(tagService).delete(1L);
    }
}
