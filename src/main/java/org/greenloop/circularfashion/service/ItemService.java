package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.enums.ItemStatus;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Item create(Item item);
    Optional<Item> getById(Long id);
    List<Item> getByOwner(Long ownerId);
    List<Item> getByStatus(ItemStatus status);
    List<Item> getByOwnerAndStatus(Long ownerId, ItemStatus status);
    List<Item> getByCategory(Long categoryId);
    List<Item> getByBrand(Long brandId);
    Item update(Long id, Item updated);
    void delete(Long id);
} 