package com.example.project.service;

import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.User;
import com.example.project.model.Wishlist;
import com.example.project.repository.GameRepository;
import com.example.project.repository.UserRepository;
import com.example.project.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public WishlistService(WishlistRepository wishlistRepository,
            UserRepository userRepository,
            GameRepository gameRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Wishlist getOrCreateByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist wishlist = new Wishlist();
                    wishlist.setUser(user);
                    user.setWishlist(wishlist);
                    return wishlistRepository.save(wishlist);
                });
    }

    public Set<Game> getGames(Long userId) {
        return getOrCreateByUserId(userId).getGames();
    }

    @Transactional
    public Wishlist addGame(Long userId, Long gameId) {
        Wishlist wishlist = getOrCreateByUserId(userId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));

        wishlist.addGame(game);
        return wishlist;
    }

    @Transactional
    public Wishlist removeGame(Long userId, Long gameId) {
        Wishlist wishlist = getOrCreateByUserId(userId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));

        wishlist.removeGame(game);
        return wishlist;
    }

    @Transactional(readOnly = true)
    public Wishlist getById(long id) {
        return wishlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Wishlist not found with id: " + id));
    }

    @Transactional
    public void delete(long id) {
        Wishlist wishlist = getById(id);
        wishlistRepository.delete(wishlist);
    }
}
