package com.example.project.repository;

import com.example.project.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByName(String name);

    List<Game> findByNameContainingIgnoreCase(String name);

    @Query("""
             SELECT DISTINCT g
             FROM Game g
             JOIN g.tags t
             WHERE LOWER(t.name) IN :tagNames
            """)
    List<Game> findByAnyTagNames(@Param("tagNames") List<String> tagNames);
}
