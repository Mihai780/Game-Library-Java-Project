package com.example.controller;

import com.example.project.controller.ReviewController;
import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Review;
import com.example.project.model.ReviewCreateRequestDTO;
import com.example.project.service.ReviewService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Review review;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(reviewController)
                .setValidator(validator)
                .build();

        review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Great game");
    }

    @Test
    void createReview_Success() throws Exception {
        when(reviewService.create(1L, 2L, 5, "Great game")).thenReturn(review);

        ReviewCreateRequestDTO req = new ReviewCreateRequestDTO();
        req.setRating(5);
        req.setComment("Great game");

        mockMvc.perform(post("/rest/reviews")
                .param("userId", "1")
                .param("gameId", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Great game"));

        verify(reviewService).create(1L, 2L, 5, "Great game");
    }

    @Test
    void createReview_AlreadyExists_Returns409() throws Exception {
        when(reviewService.create(1L, 2L, 5, "Great game"))
                .thenThrow(new ConflictException("User already reviewed this game"));

        mockMvc.perform(post("/rest/reviews")
                .param("userId", "1")
                .param("gameId", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":5,\"comment\":\"Great game\"}"))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConflictException));

        verify(reviewService).create(1L, 2L, 5, "Great game");
    }

    @Test
    void getAllReviews_Success() throws Exception {
        when(reviewService.getAll()).thenReturn(Arrays.asList(review));

        mockMvc.perform(get("/rest/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reviewService).getAll();
    }

    @Test
    void getReviewById_NotFound_Returns404() throws Exception {
        when(reviewService.getById(999L)).thenThrow(new NotFoundException("Review not found"));

        mockMvc.perform(get("/rest/reviews/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        verify(reviewService).getById(999L);
    }

    @Test
    void deleteReview_Success() throws Exception {
        doNothing().when(reviewService).delete(1L);

        mockMvc.perform(delete("/rest/reviews/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(reviewService).delete(1L);
    }
}
