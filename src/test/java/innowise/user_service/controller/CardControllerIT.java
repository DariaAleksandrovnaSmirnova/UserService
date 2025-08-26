package innowise.user_service.controller;

import innowise.user_service.dto.CardDto;
import innowise.user_service.dto.UserDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class CardControllerIT extends BaseIT {

    private CardDto cardDto;
    private Long userId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserDto userDto = UserDto.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        UserDto createdUser = userService.createUser(userDto);
        userId = createdUser.getId();
        cardDto = CardDto.builder()
                .userId(userId)
                .number("1234567890123456")
                .holder("John Doe")
                .expirationDate(LocalDate.now().plusYears(1))
                .build();
    }

    @Test
    void shouldCreateCard() throws Exception {
        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value(cardDto.getNumber()))
                .andExpect(jsonPath("$.holder").value(cardDto.getHolder()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldGetCardById() throws Exception {
        String createdCardJson = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andReturn().getResponse().getContentAsString();

        CardDto createdCard = objectMapper.readValue(createdCardJson, CardDto.class);

        mockMvc.perform(get("/cards/{id}", createdCard.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCard.getId()))
                .andExpect(jsonPath("$.number").value(createdCard.getNumber()));
    }

    @Test
    void shouldGetCardsByIds() throws Exception {
        String card1Json = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andReturn().getResponse().getContentAsString();

        CardDto card1 = objectMapper.readValue(card1Json, CardDto.class);

        CardDto card2Dto = CardDto.builder()
                .userId(userId)
                .number("9876543210987654")
                .holder("Jane Smith")
                .expirationDate(LocalDate.now().plusYears(1))
                .build();

        String card2Json = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card2Dto)))
                .andReturn().getResponse().getContentAsString();

        CardDto card2 = objectMapper.readValue(card2Json, CardDto.class);

        mockMvc.perform(post("/cards/list")
                        .param("ids", card1.getId().toString(), card2.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(card1.getId()))
                .andExpect(jsonPath("$[1].id").value(card2.getId()));
    }

    @Test
    void shouldUpdateCardById() throws Exception {
        String createdCardJson = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andReturn().getResponse().getContentAsString();

        CardDto createdCard = objectMapper.readValue(createdCardJson, CardDto.class);

        CardDto updateDto = CardDto.builder()
                .userId(userId)
                .number("9999888877776666")
                .holder("Updated Holder")
                .expirationDate(LocalDate.now().plusYears(1))
                .build();

        mockMvc.perform(put("/cards/{id}", createdCard.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("9999888877776666"))
                .andExpect(jsonPath("$.holder").value("Updated Holder"))
                .andExpect(jsonPath("$.id").value(createdCard.getId()));
    }

    @Test
    void shouldDeleteCardById() throws Exception {
        String createdCardJson = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andReturn().getResponse().getContentAsString();

        CardDto createdCard = objectMapper.readValue(createdCardJson, CardDto.class);

        mockMvc.perform(delete("/cards/{id}", createdCard.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cards/{id}", createdCard.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForNonExistentCard() throws Exception {
        mockMvc.perform(get("/cards/9999"))
                .andExpect(status().isNotFound());
    }
}