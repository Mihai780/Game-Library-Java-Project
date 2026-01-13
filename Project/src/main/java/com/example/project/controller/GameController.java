package com.example.project.controller;

import com.example.project.model.Game;
import com.example.project.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/games")
@Tag(name = "Game Management", description = "APIs for managing games.")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "Create a new game", description = "Creates a new game. The game name must be unique. Returns the created game.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Game created successfully."),
            @ApiResponse(responseCode = "400", description = "Validation failed."),
            @ApiResponse(responseCode = "409", description = "Game with this name already exists.")
    })
    @PostMapping
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) {
        Game saved = gameService.create(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Get all games", description = "Returns the complete list of games. If there are no games, an empty list is returned.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all games.")
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        return ResponseEntity.ok(gameService.getAll());
    }

    @Operation(summary = "Get game by ID", description = "Retrieves a single game by its unique ID. Returns 404 if the game does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Game found and returned."),
            @ApiResponse(responseCode = "404", description = "Game not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getById(id));
    }

    @Operation(summary = "Delete a game", description = "Deletes the game with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Game deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Game not found.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search games by name (partial match)", description = "Searches games by name using a case-insensitive partial match.")
    @ApiResponse(responseCode = "200", description = "Search completed successfully.")
    @GetMapping("/search/name")
    public ResponseEntity<List<Game>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(gameService.searchByName(name));
    }

    @Operation(summary = "Search games by tags", description = "Returns games that have at least one of the provided tag names.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search completed successfully."),
            @ApiResponse(responseCode = "400", description = "No tags provided or tags are invalid.")
    })
    @GetMapping("/search/tags")
    public ResponseEntity<List<Game>> searchByTags(@RequestParam List<String> tags) {
        return ResponseEntity.ok(gameService.searchByTags(tags));
    }

    @Operation(summary = "Add a tag to a game", description = "Associates an existing tag with an existing game. Returns the updated game.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag added to game."),
            @ApiResponse(responseCode = "404", description = "Game or Tag not found.")
    })
    @PostMapping("/{gameId}/tags/{tagId}")
    public ResponseEntity<Game> addTagToGame(@PathVariable Long gameId, @PathVariable Long tagId) {
        return ResponseEntity.ok(gameService.addTag(gameId, tagId));
    }

    @Operation(summary = "Remove a tag from a game", description = "Removes an association between a tag and a game. Returns the updated game.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag removed from game."),
            @ApiResponse(responseCode = "404", description = "Game or Tag not found.")
    })
    @DeleteMapping("/{gameId}/tags/{tagId}")
    public ResponseEntity<Game> removeTagFromGame(@PathVariable Long gameId, @PathVariable Long tagId) {
        return ResponseEntity.ok(gameService.removeTag(gameId, tagId));
    }
}
