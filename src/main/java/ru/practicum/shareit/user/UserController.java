package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto dto) {
        log.debug("POST /users body={}", dto);
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        log.debug("GET /users/{}", id);
        return service.get(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("GET /users");
        return service.getAll();
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @RequestBody UserDto patchDto) {
        log.debug("PATCH /users/{} patch={}", id, patchDto);
        return service.update(id, patchDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.debug("DELETE /users/{}", id);
        service.delete(id);
    }
}