package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void createItem() {
        UserDto user = userService.create(new UserDto(null, "Owner", "owner@mail.com"));
        ItemDto itemDto = new ItemDto(null, "Item", "Desc", true, null);

        ItemDto createdItem = itemService.create(user.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", createdItem.getId()).getSingleResult();

        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getOwner().getId(), equalTo(user.getId()));
    }

    @Test
    void getOwnerItems() {
        UserDto user = userService.create(new UserDto(null, "Owner2", "owner2@mail.com"));
        itemService.create(user.getId(), new ItemDto(null, "Item1", "Desc1", true, null));
        itemService.create(user.getId(), new ItemDto(null, "Item2", "Desc2", true, null));

        var items = itemService.getOwnerItems(user.getId());

        assertThat(items, hasSize(2));
    }
}