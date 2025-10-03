package org.greenloop.circularfashion.enums;

public enum TransactionType {
    SALE("Sale"),
    RENTAL("Rental"),
    TRADE("Trade"),
    COLLECTION_REWARD("Collection Reward"),
    POINT_REDEMPTION("Point Redemption"),
    REFUND("Refund");
    
    private final String displayName;
    
    TransactionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 