package org.greenloop.circularfashion.exception;

import java.util.UUID;

public class UnauthorizedItemAccessException extends RuntimeException {
    
    public UnauthorizedItemAccessException(UUID itemId, UUID userId) {
        super(String.format("User %s is not authorized to access item %s", userId, itemId));
    }
    
    public UnauthorizedItemAccessException(String message) {
        super(message);
    }
}










