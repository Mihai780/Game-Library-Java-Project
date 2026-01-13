package com.example.project.controller;

import com.example.project.model.GameTag;
import com.example.project.service.GameTagService;
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
@RequestMapping("/rest/tags")
@Tag(name = "Tag Management", description = "APIs for managing game tags.")
public class GameTagController {

    private final GameTagService tagService;

    public GameTagController(GameTagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "Create a new tag", description = "Creates a new tag. Tag name must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tag created successfully."),
            @ApiResponse(responseCode = "400", description = "Validation failed."),
            @ApiResponse(responseCode = "409", description = "Tag with this name already exists.")
    })
    @PostMapping
    public ResponseEntity<GameTag> createTag(@Valid @RequestBody GameTag tag) {
        GameTag saved = tagService.create(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Get all tags", description = "Returns all tags.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all tags.")
    @GetMapping
    public ResponseEntity<List<GameTag>> getAllTags() {
        return ResponseEntity.ok(tagService.getAll());
    }

    @Operation(summary = "Get tag by ID", description = "Returns a tag by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag found and returned."),
            @ApiResponse(responseCode = "404", description = "Tag not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GameTag> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getById(id));
    }

    @Operation(summary = "Delete a tag", description = "Deletes the tag with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Tag not found.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
