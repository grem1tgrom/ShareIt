package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId,
                          @Valid @RequestBody ItemDto dto) {
        log.debug("POST /items userId={} body={}", userId, dto);
        return service.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto patchDto) {
        log.debug("PATCH /items/{} userId={} patch={}", itemId, userId, patchDto);
        return service.update(userId, itemId, patchDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(USER_HEADER) Long userId,
                       @PathVariable Long itemId) {
        log.debug("GET /items/{} userId={}", itemId, userId);
        return service.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader(USER_HEADER) Long userId) {
        log.debug("GET /items userId={}", userId);
        return service.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        log.debug("GET /items/search text='{}'", text);
        return service.search(text);
    }
}