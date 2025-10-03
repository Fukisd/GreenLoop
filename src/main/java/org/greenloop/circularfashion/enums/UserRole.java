package org.greenloop.circularfashion.enums;

public enum UserRole {
    ADMIN("Admin"),
    USER("User"),
    COLLECTOR("Collector"),
    MODERATOR("Moderator");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 