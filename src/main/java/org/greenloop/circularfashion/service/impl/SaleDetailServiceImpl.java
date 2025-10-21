package org.greenloop.circularfashion.service.impl;

import lombok.RequiredArgsConstructor;
import org.greenloop.circularfashion.dto.saledetail.SaleDetailResponse;
import org.greenloop.circularfashion.entity.Sale;
import org.greenloop.circularfashion.entity.SaleDetail;
import org.greenloop.circularfashion.entity.SaleItem;
import org.greenloop.circularfashion.repository.SaleDetailRepository;
import org.greenloop.circularfashion.repository.SaleItemRepository;
import org.greenloop.circularfashion.repository.SaleRepository;
import org.greenloop.circularfashion.service.SaleDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleDetailServiceImpl implements SaleDetailService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final SaleItemRepository saleItemRepository;

    @Override
    public List<SaleDetailResponse> findAll() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SaleDetailResponse findBySaleId(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + saleId));
        return convertToResponse(sale);
    }

    @Override
    public SaleDetail save(SaleDetail detail) {
        if (detail.getSaleItem() != null && detail.getSaleItem().getId() != null) {
            SaleItem item = saleItemRepository.findById(detail.getSaleItem().getId())
                    .orElseThrow(() -> new RuntimeException("SaleItem not found with id: " + detail.getSaleItem().getId()));
            detail.setSaleItem(item);
        }

        if (detail.getSale() != null && detail.getSale().getId() != null) {
            Sale sale = saleRepository.findById(detail.getSale().getId())
                    .orElseThrow(() -> new RuntimeException("Sale not found with id: " + detail.getSale().getId()));
            detail.setSale(sale);
        }

        return saleDetailRepository.save(detail);
    }

    @Override
    public SaleDetail update(Long id, SaleDetail updatedDetail) {
        SaleDetail existingDetail = saleDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SaleDetail not found with id: " + id));

        existingDetail.setQuantity(updatedDetail.getQuantity());
        existingDetail.setPrice(updatedDetail.getPrice());

        if (updatedDetail.getSaleItem() != null && updatedDetail.getSaleItem().getId() != null) {
            SaleItem item = saleItemRepository.findById(updatedDetail.getSaleItem().getId())
                    .orElseThrow(() -> new RuntimeException("SaleItem not found with id: " + updatedDetail.getSaleItem().getId()));
            existingDetail.setSaleItem(item);
        }

        return saleDetailRepository.save(existingDetail);
    }

    @Override
    public void delete(Long id) {
        SaleDetail detail = saleDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SaleDetail not found with id: " + id));
        saleDetailRepository.delete(detail);
    }

    private SaleDetailResponse convertToResponse(Sale sale) {
        return SaleDetailResponse.builder()
                .saleId(sale.getId())
                .buyerId(sale.getBuyer() != null ? sale.getBuyer().getUserId() : null)
                .buyerName(sale.getBuyer() != null
                        ? sale.getBuyer().getFirstName() + " " + sale.getBuyer().getLastName()
                        : null)
                .saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount())
                .details(
                        sale.getDetails().stream()
                                .map(detail -> SaleDetailResponse.DetailInfo.builder()
                                        .saleDetailId(detail.getId())
                                        .saleItemId(detail.getSaleItem() != null ? detail.getSaleItem().getId() : null)
                                        .saleItemName(detail.getSaleItem() != null ? detail.getSaleItem().getName() : null)
                                        .saleItemDescription(detail.getSaleItem() != null ? detail.getSaleItem().getDescription() : null)
                                        .quantity(detail.getQuantity())
                                        .unitPrice(detail.getPrice() / detail.getQuantity())
                                        .price(detail.getPrice())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
