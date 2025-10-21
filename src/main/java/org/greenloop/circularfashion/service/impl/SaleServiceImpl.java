package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.*;
import org.greenloop.circularfashion.entity.request.SaleRequest;
import org.greenloop.circularfashion.repository.*;
import org.greenloop.circularfashion.service.SaleService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Sale> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        sales.forEach(sale -> {
            if (sale.getBuyer() != null) sale.getBuyer().getUserId();
            sale.getDetails().forEach(detail -> {
                if (detail.getSaleItem() != null) {
                    detail.getSaleItem().getId();   // ép load
                    detail.getSaleItem().getName(); // ép load
                }
            });
        });

        return sales;
    }

    @Override
    @Transactional(readOnly = true)
    public Sale getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        if (sale.getBuyer() != null) sale.getBuyer().getUserId();
        sale.getDetails().forEach(detail -> {
            if (detail.getSaleItem() != null) {
                detail.getSaleItem().getId();
                detail.getSaleItem().getName();
            }
        });

        return sale;
    }

    @Override
    @Transactional
    public Sale createSale(SaleRequest request) {
        User buyer = userRepository.findById(UUID.fromString(request.getBuyerId()))
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Sale sale = new Sale();
        sale.setBuyer(buyer);

        if (request.getSaleDate() != null) {
            sale.setSaleDate(request.getSaleDate());
        } else {
            sale.setSaleDate(LocalDateTime.now());
        }

        double total = 0.0;
        List<SaleDetail> details = new ArrayList<>();

        for (SaleRequest.SaleDetailRequest detailReq : request.getSaleDetails()) {
            SaleItem saleItem = saleItemRepository.findById(detailReq.getSaleItemId())
                    .orElseThrow(() -> new RuntimeException("Sale item not found"));

            if (saleItem.getQuantity() < detailReq.getQuantity()) {
                throw new RuntimeException("Not enough quantity for item: " + saleItem.getName());
            }

            saleItem.setQuantity(saleItem.getQuantity() - detailReq.getQuantity());
            saleItemRepository.save(saleItem);

            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setSaleItem(saleItem);
            detail.setQuantity(detailReq.getQuantity());
            detail.setPrice(saleItem.getPrice() * detailReq.getQuantity());

            total += detail.getPrice();
            details.add(detail);
        }

        sale.setDetails(details);
        sale.setTotalAmount(total);

        return saleRepository.save(sale);
    }

    @Override
    @Transactional
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        for (SaleDetail detail : sale.getDetails()) {
            SaleItem saleItem = detail.getSaleItem();
            saleItem.setQuantity(saleItem.getQuantity() + detail.getQuantity());
            saleItemRepository.save(saleItem);
        }

        saleRepository.delete(sale);
    }
}
