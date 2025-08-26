package innowise.user_service.service;

import innowise.user_service.dto.CardDto;
import innowise.user_service.entity.Card;
import innowise.user_service.mapper.CardMapperImpl;
import innowise.user_service.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Spy
    private CardMapperImpl cardMapper = new CardMapperImpl();

    @InjectMocks
    private CardService cardService;

    private CardDto cardDto;
    private Card card;

    @BeforeEach
    void setUp() {
        cardDto = CardDto.builder()
                .id(1L)
                .number("1234567890123456")
                .holder("John Doe")
                .expirationDate(LocalDate.of(2025, 12, 22))
                .build();

        card = Card.builder()
                .id(1L)
                .number("1234567890123456")
                .holder("John Doe")
                .expirationDate(LocalDate.of(2025, 12, 22))
                .build();
    }

    @Test
    void testCreateCard() {
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        CardDto result = cardService.createCard(cardDto);

        assertNotNull(result);
        assertEquals(cardDto.getId(), result.getId());
        assertEquals(cardDto.getNumber(), result.getNumber());
        assertEquals(cardDto.getHolder(), result.getHolder());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void testGetCardById() {
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardDto result = cardService.getCardById(cardId);

        assertNotNull(result);
        assertEquals(cardDto.getId(), result.getId());
        assertEquals(cardDto.getNumber(), result.getNumber());
        assertEquals(cardDto.getHolder(), result.getHolder());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void testGetCardByIdWithNonExistingCard() {
        Long cardId = 999L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cardService.getCardById(cardId));

        assertEquals("There is no card with id 999", exception.getMessage());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void testGetCardsByIds() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<Card> cards = Arrays.asList(card, card, card);

        when(cardRepository.findAllById(ids)).thenReturn(cards);

        List<CardDto> result = cardService.getCardsByIds(ids);

        assertNotNull(result);
        assertEquals(3, result.size());
        result.forEach(dto -> {
            assertEquals(cardDto.getId(), dto.getId());
            assertEquals(cardDto.getNumber(), dto.getNumber());
        });
        verify(cardRepository).findAllById(ids);
    }

    @Test
    void testGetCardsByIdsWithNonExistingCard() {
        List<Long> ids = Arrays.asList(999L, 1000L);
        when(cardRepository.findAllById(ids)).thenReturn(List.of());

        List<CardDto> result = cardService.getCardsByIds(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cardRepository).findAllById(ids);
    }

    @Test
    void updateCardById() {
    }

    @Test
    void testUpdateCardById() {
        Long cardId = 1L;
        CardDto updatedCardDto = CardDto.builder()
                .id(1L)
                .number("9876543210987654")
                .holder("Jane Smith")
                .expirationDate(LocalDate.of(2026, 6, 22))
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        CardDto result = cardService.updateCardById(updatedCardDto, cardId);

        assertNotNull(result);
        assertEquals(updatedCardDto.getNumber(), result.getNumber());
        assertEquals(updatedCardDto.getHolder(), result.getHolder());
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(card);
    }

    @Test
    void updateCardById_WhenCardNotExists_ShouldThrowEntityNotFoundException() {
        Long cardId = 999L;
        CardDto updatedCardDto = CardDto.builder()
                .id(1L)
                .number("9876543210987654")
                .holder("Jane Smith")
                .expirationDate(LocalDate.of(2026, 6, 22))
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cardService.updateCardById(updatedCardDto, cardId));

        assertEquals("There is no card with id 999", exception.getMessage());
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void testDeleteCardById() {
        Long cardId = 1L;
        when(cardRepository.existsById(cardId)).thenReturn(true);

        cardService.deleteCardById(cardId);

        verify(cardRepository).existsById(cardId);
        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void testDeleteCardByIdWithNonExistingCard() {
        Long cardId = 999L;
        when(cardRepository.existsById(cardId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cardService.deleteCardById(cardId));

        assertEquals("There is no card with id 999", exception.getMessage());
        verify(cardRepository).existsById(cardId);
        verify(cardRepository, never()).deleteById(cardId);
    }
}