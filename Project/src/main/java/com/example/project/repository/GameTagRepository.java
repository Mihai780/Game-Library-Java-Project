package com.example.project.repository;

import com.example.project.model.GameTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameTagRepository extends JpaRepository<GameTag, Long> {
    Optional<GameTag> findByNameIgnoreCase(String name);
}
