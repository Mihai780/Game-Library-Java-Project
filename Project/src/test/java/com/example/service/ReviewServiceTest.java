package com.example.service;

import com.example.project.exception.ConflictException;
import com.example.project.exception.NotFoundException;
import com.example.project.model.Game;
import com.example.project.model.Review;
import com.example.project.model.User;
import com.example.project.repository.GameRepository;
import com.example.project.repository.ReviewRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.ReviewService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Game game;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("mihai");

        game = new Game();
        game.setName("Hades");
    }

    @Test
    void createReview_Success() {
        when(reviewRepository.findByUserIdAndGameId(1L, 2L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.create(1L, 2L, 5, "Amazing");

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(game, result.getGame());
        assertEquals(5, result.getRating());
        assertEquals("Amazing", result.getComment());

        verify(reviewRepository).findByUserIdAndGameId(1L, 2L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_Duplicate_ThrowsConflict() {
        when(reviewRepository.findByUserIdAndGameId(1L, 2L)).thenReturn(Optional.of(new Review()));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> reviewService.create(1L, 2L, 5, "dup"));
        assertEquals("Review already exists for this user/game.", ex.getMessage());

        verify(reviewRepository).findByUserIdAndGameId(1L, 2L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_UserNotFound_Throws() {
        when(reviewRepository.findByUserIdAndGameId(1L, 2L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> reviewService.create(1L, 2L, 5, "x"));
        assertEquals("User not found.", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getAll_Success() {
        when(reviewRepository.findAll()).thenReturn(List.of(new Review()));

        List<Review> result = reviewService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository).findAll();
    }

    @Test
    void getById_NotFound_Throws() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> reviewService.getById(99L));
        assertEquals("Review not found.", ex.getMessage());

        verify(reviewRepository).findById(99L);
    }

    @Test
    void update_Success() {
        Review existing = new Review();
        existing.setRating(2);
        existing.setComment("old");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.update(1L, 4, "new");

        assertNotNull(result);
        assertEquals(4, result.getRating());
        assertEquals("new", result.getComment());

        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(existing);
    }

    @Test
    void delete_Success() {
        when(reviewRepository.existsById(1L)).thenReturn(true);

        reviewService.delete(1L);

        verify(reviewRepository).existsById(1L);
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void delete_NotFound_Throws() {
        when(reviewRepository.existsById(999L)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> reviewService.delete(999L));
        assertEquals("Review not found.", ex.getMessage());

        verify(reviewRepository).existsById(999L);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}
