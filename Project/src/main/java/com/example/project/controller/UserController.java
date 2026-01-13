package com.example.project.controller;

import com.example.project.model.BalanceTopUpRequestDTO;
import com.example.project.model.Game;
import com.example.project.model.User;
import com.example.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/rest/users")
@io.swagger.v3.oas.annotations.tags.Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @Operation(summary = "Create a new user", description = "Creates a new user.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "User created successfully."),
                        @ApiResponse(responseCode = "400", description = "Validation failed."),
                        @ApiResponse(responseCode = "409", description = "Username already exists.")
        })
        @PostMapping
        public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
                User saved = userService.create(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }

        @Operation(summary = "Get all users", description = "Returns all users.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all users.")
        @GetMapping
        public ResponseEntity<List<User>> getAllUsers() {
                return ResponseEntity.ok(userService.getAll());
        }

        @Operation(summary = "Get user by ID", description = "Returns a user by ID. Returns 404 if not found.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User found and returned."),
                        @ApiResponse(responseCode = "404", description = "User not found.")
        })
        @GetMapping("/{id}")
        public ResponseEntity<User> getUserById(@PathVariable Long id) {
                return ResponseEntity.ok(userService.getById(id));
        }

        @Operation(summary = "Get user by username", description = "Returns a user by username.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User found and returned."),
                        @ApiResponse(responseCode = "404", description = "User not found.")
        })
        @GetMapping("/by-username/{username}")
        public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
                return ResponseEntity.ok(userService.getByUsername(username));
        }

        @Operation(summary = "Get user's owned games", description = "Returns the set of games owned by the user.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Owned games returned."),
                        @ApiResponse(responseCode = "404", description = "User not found.")
        })
        @GetMapping("/{userId}/owned-games")
        public ResponseEntity<Set<Game>> getOwnedGames(@PathVariable Long userId) {
                return ResponseEntity.ok(userService.getOwnedGames(userId));
        }

        @Operation(summary = "Add a game to user's owned games", description = "Associates an existing game to the user's owned games.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Game added to user."),
                        @ApiResponse(responseCode = "404", description = "User or Game not found.")
        })
        @PostMapping("/{userId}/owned-games/{gameId}")
        public ResponseEntity<User> addOwnedGame(@PathVariable Long userId, @PathVariable Long gameId) {
                return ResponseEntity.ok(userService.addOwnedGame(userId, gameId));
        }

        @Operation(summary = "Remove a game from user's owned games", description = "Removes the association between a user and a game.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Game removed from user."),
                        @ApiResponse(responseCode = "404", description = "User or Game not found.")
        })
        @DeleteMapping("/{userId}/owned-games/{gameId}")
        public ResponseEntity<User> removeOwnedGame(@PathVariable Long userId, @PathVariable Long gameId) {
                return ResponseEntity.ok(userService.removeOwnedGame(userId, gameId));
        }

        @Operation(summary = "Top up user balance", description = "Adds amountCents to the user's balance.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Balance updated."),
                        @ApiResponse(responseCode = "400", description = "Validation failed."),
                        @ApiResponse(responseCode = "404", description = "User not found.")
        })
        @PostMapping("/{id}/balance")
        public ResponseEntity<User> topUpBalance(@PathVariable Long id,
                        @Valid @RequestBody BalanceTopUpRequestDTO request) {
                return ResponseEntity.ok(userService.topUpBalance(id, request.getAmountCents()));
        }
}
