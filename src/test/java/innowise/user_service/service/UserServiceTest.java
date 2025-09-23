package innowise.user_service.service;

import innowise.user_service.dto.UserDto;
import innowise.user_service.entity.User;
import innowise.user_service.mapper.UserMapperImpl;
import innowise.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper = new UserMapperImpl();

    @InjectMocks
    private UserService userService;

    public static final Long USER_ID = 1L;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(USER_ID)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .cardIds(List.of(1L, 2L))
                .build();

        user = User.builder()
                .id(USER_ID)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .cards(List.of())
                .build();
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getSurname(), result.getSurname());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userMapper).toEntity(userDto);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(user);
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(USER_ID);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getSurname(), result.getSurname());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository).findById(USER_ID);
        verify(userMapper).toDto(user);
    }

    @Test
    void testGetUserByIdWithNonExistingUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(USER_ID));
        verify(userRepository).findById(USER_ID);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testGetUsersByIds() {
        List<Long> ids = List.of(1L, 2L);
        List<User> users = List.of(user);
        when(userRepository.findAllById(ids)).thenReturn(users);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getName(), result.getFirst().getName());
        verify(userRepository).findAllById(ids);
        verify(userMapper).toDto(user);
    }

    @Test
    void getUsersByIdsWithEmptyList() {
        List<Long> ids = List.of(1L, 2L);
        when(userRepository.findAllById(ids)).thenReturn(List.of());

        List<UserDto> result = userService.getUsersByIds(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAllById(ids);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testGetUserByEmail() {
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getSurname(), result.getSurname());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository).findByEmail(email);
        verify(userMapper).toDto(user);
    }

    @Test
    void testGetUserByEmailWithNonExistingUser() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmail(email));
        verify(userRepository).findByEmail(email);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testUpdateUserById() {
        UserDto updatedUserDto = UserDto.builder()
                .id(USER_ID)
                .name("John Updated")
                .surname("Doe Updated")
                .email("john.updated@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.updateUserById(updatedUserDto, USER_ID);

        assertNotNull(result);
        assertEquals(updatedUserDto.getName(), result.getName());
        assertEquals(updatedUserDto.getSurname(), result.getSurname());
        assertEquals(updatedUserDto.getEmail(), result.getEmail());
        verify(userRepository).findById(USER_ID);
        verify(userMapper).updateEntityFromDto(updatedUserDto, user);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void testUpdateUserByIdWithNonExistingUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserById(userDto, USER_ID));
        verify(userRepository).findById(USER_ID);
        verify(userMapper, never()).updateEntityFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUserById() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        userService.deleteUserById(USER_ID);

        verify(userRepository).existsById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void testDeleteUserByIdWithNonExistingUser() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(USER_ID));
        verify(userRepository).existsById(USER_ID);
        verify(userRepository, never()).deleteById(any());
    }
}