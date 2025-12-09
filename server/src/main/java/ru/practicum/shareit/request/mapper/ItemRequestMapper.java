package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest request, List<ItemDto> items) {
        if (request == null) return null;
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items != null ? items : Collections.emptyList()
        );
    }

    public static ItemRequest toModel(ItemRequestDto dto) {
        if (dto == null) return null;
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        return request;
    }
}