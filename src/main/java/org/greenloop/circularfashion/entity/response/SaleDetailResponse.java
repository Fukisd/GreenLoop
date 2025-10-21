package org.greenloop.circularfashion.dto.saledetail;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SaleDetailResponse {
    private Long saleId;
    private UUID buyerId;
    private String buyerName;
    private LocalDateTime saleDate;
    private double totalAmount;
    private List<DetailInfo> details;

    @Data
    @Builder
    public static class DetailInfo {
        private Long saleDetailId;
        private Long saleItemId;
        private String saleItemName;
        private String saleItemDescription;
        private int quantity;
        private double unitPrice;
        private double price;
    }
}
