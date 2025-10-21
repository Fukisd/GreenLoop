package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.greenloop.circularfashion.dto.saledetail.SaleDetailResponse;
import org.greenloop.circularfashion.entity.SaleDetail;
import org.greenloop.circularfashion.service.SaleDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale-details")
@Tag(name = "Sale Detail", description = "APIs for managing sale detail")
@RequiredArgsConstructor
public class SaleDetailController {

    private final SaleDetailService saleDetailService;

    @GetMapping
    public ResponseEntity<List<SaleDetailResponse>> getAll() {
        return ResponseEntity.ok(saleDetailService.findAll());
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<SaleDetailResponse> getBySaleId(@PathVariable Long saleId) {
        return ResponseEntity.ok(saleDetailService.findBySaleId(saleId));
    }

    @PostMapping
    public ResponseEntity<SaleDetail> create(@RequestBody SaleDetail detail) {
        return ResponseEntity.ok(saleDetailService.save(detail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDetail> update(@PathVariable Long id, @RequestBody SaleDetail detail) {
        return ResponseEntity.ok(saleDetailService.update(id, detail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        saleDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
