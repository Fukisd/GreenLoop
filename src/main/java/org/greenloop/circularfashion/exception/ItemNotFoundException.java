package org.greenloop.circularfashion.exception;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
    
    public ItemNotFoundException(UUID id) {
        super("Item not found with id: " + id);
    }
    
    public ItemNotFoundException(String itemCode) {
        super("Item not found with code: " + itemCode);
    }
    
    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}










