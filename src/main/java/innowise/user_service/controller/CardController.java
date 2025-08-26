package innowise.user_service.controller;

import innowise.user_service.dto.CardDto;
import innowise.user_service.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardDto> createCard(@RequestBody @Valid CardDto CardDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.createCard(CardDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable @NotNull @Positive Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @PostMapping("/list")
    public ResponseEntity<List<CardDto>> getCardsByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(cardService.getCardsByIds(ids));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDto> updateCardById(@RequestBody @Valid CardDto CardDto,
                                                  @PathVariable @NotNull @Positive Long id) {
        return ResponseEntity.ok(cardService.updateCardById(CardDto, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCardById(@PathVariable @NotNull @Positive Long id) {
        cardService.deleteCardById(id);
    }

}
