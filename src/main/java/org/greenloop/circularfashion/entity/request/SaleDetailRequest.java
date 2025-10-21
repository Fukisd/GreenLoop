package org.greenloop.circularfashion.entity.request;

import lombok.Data;

@Data
public class SaleDetailRequest {
    private Long saleId;
    private Long saleItemId;
    private int quantity;
    private double price;
}
