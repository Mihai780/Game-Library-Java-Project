package com.example.project.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.GameTag;
import com.example.project.repository.GameTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameTagService {

    private final GameTagRepository gameTagRepository;

    public GameTagService(GameTagRepository gameTagRepository) {
        this.gameTagRepository = gameTagRepository;
    }

    public GameTag create(GameTag tag) {
        if (tag.getName() != null && gameTagRepository.findByNameIgnoreCase(tag.getName()).isPresent()) {
            throw new ConflictException("Tag with this name already exists.");
        }
        return gameTagRepository.save(tag);
    }

    public List<GameTag> getAll() {
        return gameTagRepository.findAll();
    }

    public GameTag getById(Long id) {
        return gameTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found."));
    }

    public void delete(Long id) {
        if (!gameTagRepository.existsById(id)) {
            throw new NotFoundException("Tag not found.");
        }
        gameTagRepository.deleteById(id);
    }
}
