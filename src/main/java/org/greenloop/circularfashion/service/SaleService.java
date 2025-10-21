package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.Sale;
import org.greenloop.circularfashion.entity.request.SaleRequest;
import java.util.List;

public interface SaleService {
    List<Sale> getAllSales();
    Sale getSaleById(Long id);
    Sale createSale(SaleRequest request);
    void deleteSale(Long id);
}
