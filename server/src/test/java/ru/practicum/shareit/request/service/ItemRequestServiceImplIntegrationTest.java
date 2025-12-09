package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void createRequest() {
        // Given
        UserDto userDto = new UserDto(null, "Ivan", "ivan@email.com");
        UserDto createdUser = userService.create(userDto);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужна газонокосилка");

        // When
        ItemRequestDto createdRequest = itemRequestService.create(createdUser.getId(), requestDto);

        // Then
        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", createdRequest.getId()).getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getRequestor().getName(), equalTo(createdUser.getName()));
    }

    @Test
    void getUserRequests() {
        // Given
        UserDto requestor = userService.create(new UserDto(null, "Requestor", "req@mail.com"));
        itemRequestService.create(requestor.getId(), new ItemRequestDto(null, "Request 1", null, null));
        itemRequestService.create(requestor.getId(), new ItemRequestDto(null, "Request 2", null, null));

        // When
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(requestor.getId());

        // Then
        assertThat(requests, hasSize(2));
        assertThat(requests.get(0).getDescription(), equalTo("Request 2")); // Сортировка от новых к старым
        assertThat(requests.get(1).getDescription(), equalTo("Request 1"));
    }
}