package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(name = "game_tags", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonIgnoreProperties("games")
    private Set<GameTag> tags = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "ownedGames")
    private Set<User> owners = new HashSet<>();

    public Set<User> getOwners() {
        return owners;
    }

    public Game() {
    }

    public Game(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<GameTag> getTags() {
        return tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(Set<GameTag> tags) {
        this.tags = tags;
    }

    public void addTag(GameTag tag) {
        this.tags.add(tag);
        tag.getGames().add(this);
    }

    public void removeTag(GameTag tag) {
        this.tags.remove(tag);
        tag.getGames().remove(this);
    }

    public void setId(Long id) {
        this.id = id;
    }
}
