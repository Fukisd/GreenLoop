package org.greenloop.circularfashion.entity.response;

import lombok.Data;

@Data
public class SaleItemResponse {
    private Long id;
    private String productName;
    private Integer quantity;
    private Double price;
}
