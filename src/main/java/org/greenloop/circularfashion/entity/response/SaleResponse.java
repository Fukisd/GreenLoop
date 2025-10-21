package org.greenloop.circularfashion.entity.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SaleResponse {
    private Long id;
    private UUID buyerId;
    private LocalDateTime saleDate;
    private double totalAmount;
    private List<SaleDetailResponse> details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<SaleDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetailResponse> details) {
        this.details = details;
    }

    public static class SaleDetailResponse {
        private Long saleItemId;
        private String saleItemName;
        private int quantity;
        private double price;

        public Long getSaleItemId() {
            return saleItemId;
        }

        public void setSaleItemId(Long saleItemId) {
            this.saleItemId = saleItemId;
        }

        public String getSaleItemName() {
            return saleItemName;
        }

        public void setSaleItemName(String saleItemName) {
            this.saleItemName = saleItemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
