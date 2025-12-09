package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание запроса не должно быть пустым")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}