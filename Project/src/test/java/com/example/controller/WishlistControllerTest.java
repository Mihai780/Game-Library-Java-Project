package com.example.controller;

import com.example.project.controller.WishlistController;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.User;
import com.example.project.model.Wishlist;
import com.example.project.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    private MockMvc mockMvc;

    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(wishlistController).build();

        User user = new User();
        user.setId(1L);
        user.setUsername("mihai");

        wishlist = new Wishlist();
        wishlist.setId(1L);
        wishlist.setUser(user);
    }

    @Test
    void getOrCreateWishlist_Success() throws Exception {
        when(wishlistService.getOrCreateByUserId(1L)).thenReturn(wishlist);

        mockMvc.perform(get("/rest/wishlists/user/{userId}", 1L))
                .andExpect(status().isOk());

        verify(wishlistService).getOrCreateByUserId(1L);
    }

    @Test
    void getWishlistGames_Success() throws Exception {
        Game game = new Game();
        game.setId(5L);
        game.setName("Hollow Knight");

        Set<Game> games = new HashSet<>();
        games.add(game);

        when(wishlistService.getGames(1L)).thenReturn(games);

        mockMvc.perform(get("/rest/wishlists/user/{userId}/games", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hollow Knight"));

        verify(wishlistService).getGames(1L);
    }

    @Test
    void addGameToWishlist_Success() throws Exception {
        when(wishlistService.addGame(1L, 5L)).thenReturn(wishlist);

        mockMvc.perform(post("/rest/wishlists/user/{userId}/games/{gameId}", 1L, 5L))
                .andExpect(status().isOk());

        verify(wishlistService).addGame(1L, 5L);
    }

    @Test
    void removeGameFromWishlist_Success() throws Exception {
        when(wishlistService.removeGame(1L, 5L)).thenReturn(wishlist);

        mockMvc.perform(delete("/rest/wishlists/user/{userId}/games/{gameId}", 1L, 5L))
                .andExpect(status().isOk());

        verify(wishlistService).removeGame(1L, 5L);
    }

    @Test
    void getOrCreateWishlist_UserNotFound_Returns404() throws Exception {
        when(wishlistService.getOrCreateByUserId(999L)).thenThrow(new NotFoundException("User not found."));

        mockMvc.perform(get("/rest/wishlists/user/{userId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        verify(wishlistService).getOrCreateByUserId(999L);
    }
}
