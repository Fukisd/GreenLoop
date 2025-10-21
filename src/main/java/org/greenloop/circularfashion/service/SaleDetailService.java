package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.dto.saledetail.SaleDetailResponse;
import org.greenloop.circularfashion.entity.SaleDetail;

import java.util.List;

public interface SaleDetailService {
    List<SaleDetailResponse> findAll();
    SaleDetailResponse findBySaleId(Long saleId);
    SaleDetail save(SaleDetail detail);
    SaleDetail update(Long id, SaleDetail updatedDetail);
    void delete(Long id);
}
