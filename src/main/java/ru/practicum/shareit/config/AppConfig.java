package ru.practicum.shareit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Configuration
public class AppConfig {

    @Bean
    public UserStorage userStorage() {
        return new InMemoryUserStorage();
    }

    @Bean
    public ItemStorage itemStorage() {
        return new InMemoryItemStorage();
    }
}