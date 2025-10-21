package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sale_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SaleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    @JsonBackReference
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_item_id", referencedColumnName = "id", nullable = false)
    private SaleItem saleItem;

    private int quantity;
    private double price;

    @Transient
    @JsonProperty("saleDetailId")
    public Long getSaleDetailId() {
        return id;
    }

    @Transient
    @JsonProperty("saleItemId")
    public Long getSaleItemId() {
        return saleItem != null ? saleItem.getId() : null;
    }

    @Transient
    @JsonProperty("saleItemName")
    public String getSaleItemName() {
        return saleItem != null ? saleItem.getName() : null;
    }

    @Transient
    @JsonProperty("saleItemDescription")
    public String getSaleItemDescription() {
        return saleItem != null ? saleItem.getDescription() : null;
    }

    @Transient
    @JsonProperty("saleItemPrice")
    public Double getSaleItemPrice() {
        return saleItem != null ? saleItem.getPrice() : null;
    }
}
