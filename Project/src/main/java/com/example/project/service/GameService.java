package com.example.project.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.GameTag;
import com.example.project.repository.GameRepository;
import com.example.project.repository.GameTagRepository;
import com.example.project.repository.PurchaseRepository;
import com.example.project.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameTagRepository gameTagRepository;
    private final PurchaseRepository purchaseRepository;
    private final ReviewRepository reviewRepository;

    public GameService(GameRepository gameRepository,
            GameTagRepository gameTagRepository,
            PurchaseRepository purchaseRepository,
            ReviewRepository reviewRepository) {
        this.gameRepository = gameRepository;
        this.gameTagRepository = gameTagRepository;
        this.purchaseRepository = purchaseRepository;
        this.reviewRepository = reviewRepository;
    }

    public Game create(Game game) {
        if (game.getName() != null && gameRepository.findByName(game.getName()).isPresent()) {
            throw new ConflictException("Game with this name already exists.");
        }
        return gameRepository.save(game);
    }

    public List<Game> getAll() {
        return gameRepository.findAll();
    }

    public Game getById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Game not found."));
    }

    public List<Game> searchByName(String name) {
        return gameRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Game> searchByTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }

        List<String> normalized = tags.stream()
                .filter(t -> t != null && !t.isBlank())
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();

        if (normalized.isEmpty()) {
            return List.of();
        }

        return gameRepository.findByAnyTagNames(normalized);
    }

    public void delete(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new NotFoundException("Game not found.");
        }
        if (purchaseRepository.existsByGameId(id)) {
            throw new ConflictException("Cannot delete game: it has purchases.");
        }
        if (reviewRepository.existsByGameId(id)) {
            throw new ConflictException("Cannot delete game: it has reviews.");
        }
        gameRepository.deleteById(id);
    }

    public Game addTag(Long gameId, Long tagId) {
        Game game = getById(gameId);
        GameTag tag = gameTagRepository.findById(tagId).orElseThrow(() -> new NotFoundException("Tag not found."));

        game.addTag(tag);
        gameRepository.save(game);
        return game;
    }

    public Game removeTag(Long gameId, Long tagId) {
        Game game = getById(gameId);
        GameTag tag = gameTagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag not found."));

        game.removeTag(tag);
        gameRepository.save(game);
        return game;
    }
}
