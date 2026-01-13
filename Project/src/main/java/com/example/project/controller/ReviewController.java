package com.example.project.controller;

import com.example.project.model.Review;
import com.example.project.model.ReviewCreateRequestDTO;
import com.example.project.service.ReviewService;
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
@RequestMapping("/rest/reviews")
@Tag(name = "Review Management", description = "APIs for managing reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Create a review (one per user per game)", description = "Creates a new review for the given userId and gameId. ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed "),
            @ApiResponse(responseCode = "404", description = "User or Game not found"),
            @ApiResponse(responseCode = "409", description = "A review already exists for this user/game")
    })
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestParam Long userId, @RequestParam Long gameId,
            @Valid @RequestBody ReviewCreateRequestDTO request) {
        Review saved = reviewService.create(userId, gameId, request.getRating(), request.getComment());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Get all reviews", description = "Returns all reviews in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all reviews")
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    @Operation(summary = "Get review by ID", description = "Retrieves a single review by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review found and returned"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getById(id));
    }

    @Operation(summary = "Get reviews for a game", description = "Returns all reviews written for a specific gameId.")
    @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Review>> getReviewsByGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(reviewService.getByGame(gameId));
    }

    @Operation(summary = "Get reviews by a user", description = "Returns all reviews written by a specific userId.")
    @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getByUser(userId));
    }

    @Operation(summary = "Update review rating/comment", description = "Updates rating and/or comment for the review with the given ID. Rating must be between 1 and 5.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed (e.g., rating missing/out of range, comment too long)"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewCreateRequestDTO request) {
        return ResponseEntity.ok(reviewService.update(id, request.getRating(), request.getComment()));
    }

    @Operation(summary = "Delete a review", description = "Deletes the review with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
