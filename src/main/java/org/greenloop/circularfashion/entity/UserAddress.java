package org.greenloop.circularfashion.entity;

import org.greenloop.circularfashion.enums.AddressType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type")
    private AddressType addressType;
    
    @Column(name = "street_address")
    private String streetAddress;
    
    @Column(length = 100)
    private String city;
    
    @Column(name = "state_province", length = 100)
    private String stateProvince;
    
    @Column(length = 100)
    private String country;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 