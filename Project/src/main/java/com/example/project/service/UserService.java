package com.example.project.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.User;
import com.example.project.repository.GameRepository;
import com.example.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public UserService(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public User create(User user) {
        if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists.");
        }
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found."));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found."));
    }

    public Set<Game> getOwnedGames(Long userId) {
        return getById(userId).getOwnedGames();
    }

    public User addOwnedGame(Long userId, Long gameId) {
        User user = getById(userId);
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Game not found."));

        user.addOwnedGame(game);
        userRepository.save(user);
        return user;
    }

    public User removeOwnedGame(Long userId, Long gameId) {
        User user = getById(userId);
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Game not found."));

        user.removeOwnedGame(game);
        userRepository.save(user);
        return user;
    }

    public User topUpBalance(Long userId, Long amountCents) {
        User user = getById(userId);
        user.increaseBalance(amountCents);
        return userRepository.save(user);
    }
}
