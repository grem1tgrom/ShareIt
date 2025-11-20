package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toDto(Item item) {
        if (item == null) return null;
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item toModel(ItemDto dto, Long ownerId) {
        if (dto == null) return null;
        return new Item(dto.getId(), dto.getName(), dto.getDescription(), dto.getAvailable(), ownerId);
    }

    public static void merge(Item existing, ItemDto patch) {
        if (patch.getName() != null && !patch.getName().isBlank()) {
            existing.setName(patch.getName());
        }
        if (patch.getDescription() != null && !patch.getDescription().isBlank()) {
            existing.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            existing.setAvailable(patch.getAvailable());
        }
    }
}