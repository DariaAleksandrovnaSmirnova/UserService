package innowise.user_service.service;

import innowise.user_service.dto.UserDto;
import innowise.user_service.entity.User;
import innowise.user_service.mapper.UserMapper;
import innowise.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "#id")
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no user with id %d", id)));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no user with email %s", email)));
    }

    @Transactional
    @CachePut(value = "user", key = "#id")
    public UserDto updateUserById(UserDto userDto, Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no user with id %d", id)));
        userMapper.updateEntityFromDto(userDto, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @CacheEvict(value = "user", key = "#id")
    public void deleteUserById(Long id) {
        validateUserId(id);
        userRepository.deleteById(id);
    }

    public void validateUserId(Long id) {
        if (id == null) {
            throw new RuntimeException("ID can't be null");
        }
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("There is no user with id %d", id));

        }
    }
}
