package com.example.controller;

import com.example.project.controller.PurchaseController;
import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.Purchase;
import com.example.project.model.PurchaseCreateRequestDTO;
import com.example.project.model.User;
import com.example.project.service.PurchaseService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Purchase purchase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(purchaseController)
                .setValidator(validator)
                .build();

        User user = new User();
        user.setId(1L);
        user.setUsername("mihai");

        Game game = new Game();
        game.setId(2L);
        game.setName("Hades");

        purchase = new Purchase();
        purchase.setId(1L);
        purchase.setUser(user);
        purchase.setGame(game);
        purchase.setPriceCents(1999L);
    }

    @Test
    void purchaseGame_Success() throws Exception {
        when(purchaseService.create(1L, 2L, 1999L)).thenReturn(purchase);

        PurchaseCreateRequestDTO req = new PurchaseCreateRequestDTO();
        req.setPriceCents(1999L);

        mockMvc.perform(post("/rest/purchases/user/{userId}/game/{gameId}", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priceCents").value(1999));

        verify(purchaseService).create(1L, 2L, 1999L);
    }

    @Test
    void purchaseGame_UserNotFound_Returns404() throws Exception {
        when(purchaseService.create(1L, 2L, 1999L)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/rest/purchases/user/{userId}/game/{gameId}", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"priceCents\":1999}"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        verify(purchaseService).create(1L, 2L, 1999L);
    }

    @Test
    void purchaseGame_AlreadyPurchased_Returns409() throws Exception {
        when(purchaseService.create(1L, 2L, 1999L))
                .thenThrow(new ConflictException("User already purchased this game"));

        mockMvc.perform(post("/rest/purchases/user/{userId}/game/{gameId}", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"priceCents\":1999}"))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConflictException));

        verify(purchaseService).create(1L, 2L, 1999L);
    }

    @Test
    void getPurchasesByUser_Success() throws Exception {
        List<Purchase> purchases = Arrays.asList(purchase);
        when(purchaseService.getByUser(1L)).thenReturn(purchases);

        mockMvc.perform(get("/rest/purchases/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(purchaseService).getByUser(1L);
    }
}
