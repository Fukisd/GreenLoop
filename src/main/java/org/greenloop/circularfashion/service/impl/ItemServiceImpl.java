package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.repository.ItemRepository;
import org.greenloop.circularfashion.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item create(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Optional<Item> getById(UUID id) {
        return itemRepository.findById(id);
    }

    @Override
    public List<Item> getByOwner(UUID ownerId) {
        // Create a User object with the ID for the query
        User owner = new User();
        owner.setUserId(ownerId);
        return itemRepository.findByCurrentOwner(owner);
    }

    @Override
    public List<Item> getByStatus(Item.ItemStatus status) {
        return itemRepository.findByItemStatus(status);
    }

    @Override
    public List<Item> getByOwnerAndStatus(UUID ownerId, Item.ItemStatus status) {
        // Create a User object with the ID for the query
        User owner = new User();
        owner.setUserId(ownerId);
        return itemRepository.findByCurrentOwner(owner).stream()
                .filter(item -> item.getItemStatus() == status)
                .toList();
    }

    @Override
    public List<Item> getByCategory(UUID categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Item> getByBrand(UUID brandId) {
        return itemRepository.findByBrandId(brandId);
    }

    @Override
    public Item update(UUID id, Item updated) {
        return itemRepository.findById(id)
                .map(existing -> {
                    updated.setItemId(existing.getItemId());
                    return itemRepository.save(updated);
                })
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
    }

    @Override
    public void delete(UUID id) {
        itemRepository.deleteById(id);
    }
} 