package org.greenloop.circularfashion.mapper;

import org.greenloop.circularfashion.entity.Sale;
import org.greenloop.circularfashion.entity.SaleDetail;
import org.greenloop.circularfashion.entity.response.SaleResponse;

import java.util.stream.Collectors;

public class SaleMapper {

    public static SaleResponse toResponse(Sale sale) {
        SaleResponse res = new SaleResponse();
        res.setId(sale.getId());
        res.setBuyerId(sale.getBuyer() != null ? sale.getBuyer().getUserId() : null);
        res.setSaleDate(sale.getSaleDate());
        res.setTotalAmount(sale.getTotalAmount());

        if (sale.getDetails() != null) {
            res.setDetails(
                    sale.getDetails().stream()
                            .map(SaleMapper::toDetailResponse)
                            .collect(Collectors.toList())
            );
        }

        return res;
    }

    private static SaleResponse.SaleDetailResponse toDetailResponse(SaleDetail detail) {
        SaleResponse.SaleDetailResponse r = new SaleResponse.SaleDetailResponse();
        if (detail.getSaleItem() != null) {
            r.setSaleItemId(detail.getSaleItem().getId());
            r.setSaleItemName(detail.getSaleItem().getName());
        }
        r.setQuantity(detail.getQuantity());
        r.setPrice(detail.getPrice());
        return r;
    }
}
