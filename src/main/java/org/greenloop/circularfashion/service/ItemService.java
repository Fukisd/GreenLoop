package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemService {
    Item create(Item item);
    Optional<Item> getById(UUID id);
    List<Item> getByOwner(UUID ownerId);
    List<Item> getByStatus(Item.ItemStatus status);
    List<Item> getByOwnerAndStatus(UUID ownerId, Item.ItemStatus status);
    List<Item> getByCategory(UUID categoryId);
    List<Item> getByBrand(UUID brandId);
    Item update(UUID id, Item updated);
    void delete(UUID id);
} 