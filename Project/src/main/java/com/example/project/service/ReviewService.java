package com.example.project.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.Review;
import com.example.project.model.User;
import com.example.project.repository.GameRepository;
import com.example.project.repository.ReviewRepository;
import com.example.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository,
            GameRepository gameRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public Review create(Long userId, Long gameId, Integer rating, String comment) {
        if (reviewRepository.findByUserIdAndGameId(userId, gameId).isPresent()) {
            throw new ConflictException("Review already exists for this user/game.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Game not found."));

        Review review = new Review();
        review.setUser(user);
        review.setGame(game);
        review.setRating(rating);
        review.setComment(comment);

        return reviewRepository.save(review);
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new NotFoundException("Review not found."));
    }

    public List<Review> getByGame(Long gameId) {
        return reviewRepository.findByGameId(gameId);
    }

    public List<Review> getByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Review update(Long id, Integer rating, String comment) {
        Review review = getById(id);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    public void delete(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new NotFoundException("Review not found.");
        }
        reviewRepository.deleteById(id);
    }
}
