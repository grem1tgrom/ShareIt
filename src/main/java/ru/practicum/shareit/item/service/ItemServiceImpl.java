package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage,
                           UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        ensureUserExists(ownerId);
        Item item = ItemMapper.toModel(dto, ownerId);
        Item saved = itemStorage.save(item);
        log.info("Создана вещь id={} владельцем id={}", saved.getId(), ownerId);
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patchDto) {
        ensureUserExists(ownerId);
        Item existing = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ValidationException("Редактировать вещь может только владелец");
        }

        ItemMapper.merge(existing, patchDto);
        itemStorage.update(existing);
        log.info("Обновлена вещь id={} владельцем id={}", itemId, ownerId);
        return ItemMapper.toDto(existing);
    }

    @Override
    public ItemDto get(Long requesterId, Long itemId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        ensureUserExists(ownerId);
        return itemStorage.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemStorage.search(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private void ensureUserExists(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }
}