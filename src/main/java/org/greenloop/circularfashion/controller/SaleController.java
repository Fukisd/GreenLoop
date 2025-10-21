package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.greenloop.circularfashion.entity.Sale;
import org.greenloop.circularfashion.entity.request.SaleRequest;
import org.greenloop.circularfashion.entity.response.SaleResponse;
import org.greenloop.circularfashion.mapper.SaleMapper;
import org.greenloop.circularfashion.service.SaleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sale", description = "APIs for managing sale")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping
    public List<SaleResponse> getAllSales() {
        return saleService.getAllSales()
                .stream()
                .map(SaleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @GetMapping("/{id}")
    public SaleResponse getSaleById(@PathVariable Long id) {
        Sale sale = saleService.getSaleById(id);
        return SaleMapper.toResponse(sale);
    }

    @PostMapping
    public SaleResponse createSale(@RequestBody SaleRequest request) {
        Sale sale = saleService.createSale(request);
        return SaleMapper.toResponse(sale);
    }

    @DeleteMapping("/{id}")
    public void deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
    }
}
