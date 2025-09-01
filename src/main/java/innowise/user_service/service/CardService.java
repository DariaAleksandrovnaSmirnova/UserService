package innowise.user_service.service;

import innowise.user_service.dto.CardDto;
import innowise.user_service.entity.Card;
import innowise.user_service.mapper.CardMapper;
import innowise.user_service.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Transactional
    public CardDto createCard(CardDto cardDto) {
        Card card = cardMapper.toEntity(cardDto);
        Card updatedCard = cardRepository.save(card);
        return cardMapper.toDto(updatedCard);
    }

    @Transactional(readOnly = true)
    public CardDto getCardById(Long id) {
        return cardRepository.findById(id)
                .map(cardMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no card with id %d", id)));
    }

    @Transactional(readOnly = true)
    public List<CardDto> getCardsByIds(List<Long> ids) {
        return cardRepository.findAllById(ids).stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Transactional
    public CardDto updateCardById(CardDto cardDto, Long id) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no card with id %d", id)));
        cardMapper.updateEntityFromDto(cardDto, existingCard);
        Card updatedCard = cardRepository.save(existingCard);
        return cardMapper.toDto(updatedCard);
    }

    @Transactional
    public void deleteCardById(Long id) {
        validateCardId(id);
        cardRepository.deleteById(id);
    }

    public void validateCardId(Long id) {
        if (id == null) {
            throw new RuntimeException("ID can't be null");
        }
        if (!cardRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("There is no card with id %d", id));

        }
    }
}
