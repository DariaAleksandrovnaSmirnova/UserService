package innowise.user_service.controller;

import innowise.user_service.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class UserControllerIT extends BaseIT {

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userDto = UserDto.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(2000, 12, 22))
                .build();
    }

    @Test
    void testCreateUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.surname").value(userDto.getSurname()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testGetUserById() throws Exception {
        String createdUserJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createdUserJson, UserDto.class);

        mockMvc.perform(get("/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()));
    }

    @Test
    void testGetUsersByIds() throws Exception {
        String user1Json = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andReturn().getResponse().getContentAsString();

        UserDto user1 = objectMapper.readValue(user1Json, UserDto.class);

        UserDto user2Dto = UserDto.builder()
                .name("Jane")
                .surname("Smith")
                .email("jane.smith@example.com")
                .birthDate(LocalDate.of(1995, 5, 15))
                .build();

        String user2Json = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2Dto)))
                .andReturn().getResponse().getContentAsString();

        UserDto user2 = objectMapper.readValue(user2Json, UserDto.class);

        mockMvc.perform(post("/users/list")
                        .param("ids", user1.getId().toString(), user2.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        String createdUserJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createdUserJson, UserDto.class);

        mockMvc.perform(get("/users/email/{email}", createdUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()))
                .andExpect(jsonPath("$.id").value(createdUser.getId()));
    }

    @Test
    void testUpdateUserById() throws Exception {
        String createdUserJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createdUserJson, UserDto.class);

        UserDto updateDto = UserDto.builder()
                .name("Updated")
                .surname("User")
                .email("updated.email@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        mockMvc.perform(put("/users/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated.email@example.com"))
                .andExpect(jsonPath("$.id").value(createdUser.getId()));
    }

    @Test
    void testDeleteUserById() throws Exception {
        String createdUserJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createdUserJson, UserDto.class);

        mockMvc.perform(delete("/users/{id}", createdUser.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{id}", createdUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testReturnNotFoundForNonExistentUser() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound());
    }
}
