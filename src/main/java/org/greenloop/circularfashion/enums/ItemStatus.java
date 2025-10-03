package org.greenloop.circularfashion.enums;

public enum ItemStatus {
    // User owned items
    OWNED("Owned by User"),
    LISTED_FOR_SALE("Listed for Sale"),
    LISTED_FOR_RENT("Listed for Rent"),
    LISTED_FOR_TRADE("Listed for Trade"),
    
    // Collection process
    PENDING_COLLECTION("Pending Collection"),
    IN_TRANSIT_TO_FACILITY("In Transit to Facility"),
    AT_COLLECTION_FACILITY("At Collection Facility"),
    UNDER_EVALUATION("Under Evaluation"),
    
    // Post-evaluation
    VERIFIED_FOR_RESALE("Verified for Resale"),
    REJECTED("Rejected"),
    RECYCLED("Recycled"),
    
    // Transaction states
    SOLD("Sold"),
    RENTED("Currently Rented"),
    TRADED("Traded"),
    RETURNED("Returned"),
    
    // System states
    INACTIVE("Inactive"),
    REPORTED("Reported"),
    BLOCKED("Blocked");
    
    private final String displayName;
    
    ItemStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 