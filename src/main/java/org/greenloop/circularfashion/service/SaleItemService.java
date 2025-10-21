package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.SaleItem;
import java.util.List;

public interface SaleItemService {
    List<SaleItem> getAllSaleItems();
    SaleItem getSaleItemById(Long id);
    SaleItem createSaleItem(SaleItem saleItem);
    SaleItem updateSaleItem(Long id, SaleItem saleItem);
    void deleteSaleItem(Long id);
}
