package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserStorage storage;

    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public UserDto create(UserDto dto) {
        if (storage.existsByEmail(dto.getEmail(), null)) {
            throw new ConflictException("Email уже используется");
        }
        User user = UserMapper.toModel(dto);
        User saved = storage.save(user);
        log.info("Создан пользователь id={}", saved.getId());
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto get(Long id) {
        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + id));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto update(Long id, UserDto patchDto) {
        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + id));

        if (patchDto.getEmail() != null &&
                storage.existsByEmail(patchDto.getEmail(), id)) {
            throw new ConflictException("Email уже используется другим пользователем");
        }

        UserMapper.merge(user, patchDto);
        storage.update(user);
        log.info("Обновлён пользователь id={}", id);
        return UserMapper.toDto(user);
    }

    @Override
    public void delete(Long id) {
        if (storage.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь не найден: id=" + id);
        }
        storage.deleteById(id);
        log.info("Удалён пользователь id={}", id);
    }
}