package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void createBooking() {
        UserDto owner = userService.create(new UserDto(null, "Owner", "owner@test.com"));
        UserDto booker = userService.create(new UserDto(null, "Booker", "booker@test.com"));
        ItemDto item = itemService.create(owner.getId(), new ItemDto(null, "Item", "Desc", true, null));

        BookingRequestDto request = new BookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        BookingDto booking = bookingService.create(booker.getId(), request);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(booking.getItem().getId(), equalTo(item.getId()));
        assertThat(booking.getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    void getBookingsByBooker() {
        UserDto owner = userService.create(new UserDto(null, "Owner2", "owner2@test.com"));
        UserDto booker = userService.create(new UserDto(null, "Booker2", "booker2@test.com"));
        ItemDto item = itemService.create(owner.getId(), new ItemDto(null, "Item2", "Desc", true, null));

        bookingService.create(booker.getId(), new BookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)));

        List<BookingDto> bookings = bookingService.getByBooker(booker.getId(), BookingState.ALL);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getItem().getName(), equalTo("Item2"));
    }
}