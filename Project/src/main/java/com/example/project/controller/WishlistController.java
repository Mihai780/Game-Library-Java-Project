package com.example.project.controller;

import com.example.project.model.Game;
import com.example.project.model.Wishlist;
import com.example.project.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/rest/wishlists")
@Tag(name = "Wishlist Management", description = "APIs for managing user wishlists")
public class WishlistController {

        private final WishlistService wishlistService;

        public WishlistController(WishlistService wishlistService) {
                this.wishlistService = wishlistService;
        }

        @Operation(summary = "Get (or create) wishlist by user ID", description = "Returns the wishlist for a user. If the user has no wishlist yet, it will be created.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Wishlist returned."),
                        @ApiResponse(responseCode = "404", description = "User not found.")
        })
        @GetMapping("/user/{userId}")
        public ResponseEntity<Wishlist> getOrCreateWishlist(@PathVariable Long userId) {
                Wishlist wishlist = wishlistService.getOrCreateByUserId(userId);
                return ResponseEntity.ok(wishlist);
        }

        @Operation(summary = "Get games from user's wishlist", description = "Returns the set of games currently in the user's wishlist. Wishlist is created automatically if missing.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Wishlist games returned."),
                        @ApiResponse(responseCode = "404", description = "User not found.")
        })
        @GetMapping("/user/{userId}/games")
        public ResponseEntity<Set<Game>> getWishlistGames(@PathVariable Long userId) {
                return ResponseEntity.ok(wishlistService.getGames(userId));
        }

        @Operation(summary = "Add game to user's wishlist", description = "Adds an existing game to the user's wishlist. Wishlist is created automatically if missing.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Game added to wishlist."),
                        @ApiResponse(responseCode = "404", description = "User or Game not found.")
        })
        @PostMapping("/user/{userId}/games/{gameId}")
        public ResponseEntity<Wishlist> addGameToWishlist(@PathVariable Long userId, @PathVariable Long gameId) {
                return ResponseEntity.ok(wishlistService.addGame(userId, gameId));
        }

        @Operation(summary = "Remove game from user's wishlist", description = "Removes an existing game from the user's wishlist. Wishlist is created automatically if missing.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Game removed from wishlist."),
                        @ApiResponse(responseCode = "404", description = "User or Game not found.")
        })
        @DeleteMapping("/user/{userId}/games/{gameId}")
        public ResponseEntity<Wishlist> removeGameFromWishlist(@PathVariable Long userId, @PathVariable Long gameId) {
                return ResponseEntity.ok(wishlistService.removeGame(userId, gameId));
        }
}
