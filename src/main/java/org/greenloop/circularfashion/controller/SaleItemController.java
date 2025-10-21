package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.greenloop.circularfashion.entity.SaleItem;
import org.greenloop.circularfashion.service.SaleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale-items")
@Tag(name = "Sale Item", description = "APIs for managing sale item")
public class SaleItemController {

    @Autowired
    private SaleItemService saleItemService;

    @GetMapping
    public ResponseEntity<List<SaleItem>> getAllSaleItems() {
        return ResponseEntity.ok(saleItemService.getAllSaleItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItem> getSaleItemById(@PathVariable Long id) {
        SaleItem item = saleItemService.getSaleItemById(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<SaleItem> createSaleItem(@RequestBody SaleItem saleItem) {
        saleItem.setId(null);
        SaleItem saved = saleItemService.createSaleItem(saleItem);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleItem> updateSaleItem(@PathVariable Long id, @RequestBody SaleItem saleItem) {
        SaleItem updated = saleItemService.updateSaleItem(id, saleItem);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Long id) {
        saleItemService.deleteSaleItem(id);
        return ResponseEntity.noContent().build();
    }
}
