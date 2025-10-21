package org.greenloop.circularfashion.entity.request;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class SaleRequest {
    private String buyerId;
    private List<SaleDetailRequest> saleDetails;
    @Schema(hidden = true)
    private LocalDateTime saleDate;

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public List<SaleDetailRequest> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetailRequest> saleDetails) {
        this.saleDetails = saleDetails;
    }

    public static class SaleDetailRequest {
        private Long saleItemId;
        private int quantity;

        public Long getSaleItemId() {
            return saleItemId;
        }

        public void setSaleItemId(Long saleItemId) {
            this.saleItemId = saleItemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
