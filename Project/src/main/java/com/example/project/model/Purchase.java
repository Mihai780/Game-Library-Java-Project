package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

@Entity
@Table(name = "purchases", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "game_id" }))
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({ "ownedGames", "wishlist" })
    private User user;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnoreProperties({ "owners", "tags" })
    private Game game;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long priceCents;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Instant purchasedAt;

    public Purchase() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }

    public Long getPriceCents() {
        return priceCents;
    }

    public Instant getPurchasedAt() {
        return purchasedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPriceCents(Long priceCents) {
        this.priceCents = priceCents;
    }

    @PrePersist
    public void onCreate() {
        this.purchasedAt = Instant.now();
    }
}
