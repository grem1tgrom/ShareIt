package ru.practicum.shareit.item.model;

public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;

    public Item() {
    }

    public Item(Long id, String name, String description, Boolean available, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public Item setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Item setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getAvailable() {
        return available;
    }

    public Item setAvailable(Boolean available) {
        this.available = available;
        return this;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Item setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
        return this;
    }
}