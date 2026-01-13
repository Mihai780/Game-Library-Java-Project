package com.example.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long balanceCents = 0L;

    @ManyToMany
    @JoinTable(name = "user_games", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
    @JsonIgnoreProperties("owners")
    private Set<Game> ownedGames = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Wishlist wishlist;

    public User() {
    }

    public User(String username) {
        this.username = username;
        this.balanceCents = 0L;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Long getBalanceCents() {
        return balanceCents;
    }

    public Set<Game> getOwnedGames() {
        return ownedGames;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBalanceCents(Long balanceCents) {
        this.balanceCents = balanceCents;
    }

    public void setOwnedGames(Set<Game> ownedGames) {
        this.ownedGames = ownedGames;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
        if (wishlist != null && wishlist.getUser() != this) {
            wishlist.setUser(this);
        }
    }

    public void addOwnedGame(Game game) {
        this.ownedGames.add(game);
        game.getOwners().add(this);
    }

    public void removeOwnedGame(Game game) {
        this.ownedGames.remove(game);
        game.getOwners().remove(this);
    }

    public void increaseBalance(Long amountCents) {
        if (amountCents == null || amountCents <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (this.balanceCents == null) {
            this.balanceCents = 0L;
        }
        this.balanceCents += amountCents;
    }

    public void decreaseBalance(Long amountCents) {
        if (amountCents == null || amountCents < 0) {
            throw new IllegalArgumentException("Amount must be non-negative.");
        }
        if (this.balanceCents == null) {
            this.balanceCents = 0L;
        }
        if (amountCents == 0) {
            return;
        }
        if (this.balanceCents < amountCents) {
            throw new IllegalStateException("Insufficient balance.");
        }
        this.balanceCents -= amountCents;
    }
}
