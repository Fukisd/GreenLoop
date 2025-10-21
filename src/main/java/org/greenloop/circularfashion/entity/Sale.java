package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    @JsonIgnore
    private User buyer;

    @Column(name = "sale_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime saleDate;

    private double totalAmount;

    @PrePersist
    public void prePersist() {
        if (this.saleDate == null) {
            this.saleDate = LocalDateTime.now();
        }
    }

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SaleDetail> details = new ArrayList<>();

    @Transient
    @JsonProperty("saleId")
    public Long getSaleId() {
        return id;
    }

    @Transient
    @JsonProperty("buyerId")
    public UUID getBuyerId() {
        return buyer != null ? buyer.getUserId() : null;
    }

    @Transient
    @JsonProperty("buyerName")
    public String getBuyerName() {
        return buyer != null ? buyer.getFullName() : null;
    }
}
