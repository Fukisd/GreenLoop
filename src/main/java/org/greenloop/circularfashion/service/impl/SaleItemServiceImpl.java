package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.SaleItem;
import org.greenloop.circularfashion.repository.SaleItemRepository;
import org.greenloop.circularfashion.service.SaleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaleItemServiceImpl implements SaleItemService {

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Override
    public List<SaleItem> getAllSaleItems() {
        return saleItemRepository.findAll();
    }

    @Override
    public SaleItem getSaleItemById(Long id) {
        Optional<SaleItem> saleItem = saleItemRepository.findById(id);
        return saleItem.orElse(null);
    }

    @Override
    public SaleItem createSaleItem(SaleItem saleItem) {
        return saleItemRepository.save(saleItem);
    }

    @Override
    public SaleItem updateSaleItem(Long id, SaleItem updatedItem) {
        return saleItemRepository.findById(id).map(item -> {
            item.setName(updatedItem.getName());
            item.setDescription(updatedItem.getDescription());
            item.setQuantity(updatedItem.getQuantity());
            item.setPrice(updatedItem.getPrice());
            return saleItemRepository.save(item);
        }).orElse(null);
    }

    @Override
    public void deleteSaleItem(Long id) {
        saleItemRepository.deleteById(id);
    }
}
