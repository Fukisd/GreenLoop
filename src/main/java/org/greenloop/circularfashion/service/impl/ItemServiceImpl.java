package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.enums.ItemStatus;
import org.greenloop.circularfashion.repository.ItemRepository;
import org.greenloop.circularfashion.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public Optional<Item> getById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        return itemRepository.findByOwnerUserId(ownerId);
    }

    @Override
    public List<Item> getByStatus(ItemStatus status) {
        return itemRepository.findByCurrentStatus(status);
    }

    @Override
    public List<Item> getByOwnerAndStatus(Long ownerId, ItemStatus status) {
        return itemRepository.findByOwnerUserIdAndCurrentStatus(ownerId, status);
    }

    @Override
    public List<Item> getByCategory(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Item> getByBrand(Long brandId) {
        return itemRepository.findByBrandId(brandId);
    }

    @Override
    public Item update(Long id, Item updated) {
        return itemRepository.findById(id)
                .map(existing -> {
                    updated.setItemId(existing.getItemId());
                    return itemRepository.save(updated);
                })
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }
} 