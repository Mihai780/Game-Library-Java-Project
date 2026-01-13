package com.example.project.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.Purchase;
import com.example.project.model.User;
import com.example.project.model.Wishlist;
import com.example.project.repository.GameRepository;
import com.example.project.repository.PurchaseRepository;
import com.example.project.repository.UserRepository;
import com.example.project.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final WishlistRepository wishlistRepository;

    public PurchaseService(PurchaseRepository purchaseRepository,
            UserRepository userRepository,
            GameRepository gameRepository,
            WishlistRepository wishlistRepository) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.wishlistRepository = wishlistRepository;
    }

    public Purchase create(Long userId, Long gameId, Long priceCents) {
        if (priceCents == null || priceCents < 0) {
            throw new ConflictException("Invalid priceCents");
        }

        if (purchaseRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new ConflictException("User already purchased this game");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));

        try {
            user.decreaseBalance(priceCents);
        } catch (IllegalStateException ex) {
            throw new ConflictException("Insufficient balance");
        } catch (IllegalArgumentException ex) {
            throw new ConflictException("Invalid priceCents");
        }

        user.addOwnedGame(game);

        Wishlist wishlist = user.getWishlist();
        if (wishlist != null) {
            wishlist.removeGame(game);
        } else if (wishlistRepository != null) {
            wishlistRepository.findByUserId(userId).ifPresent(w -> w.removeGame(game));
        }

        userRepository.save(user);

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setGame(game);
        purchase.setPriceCents(priceCents);

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        return purchaseRepository.findByUserId(userId);
    }
}
