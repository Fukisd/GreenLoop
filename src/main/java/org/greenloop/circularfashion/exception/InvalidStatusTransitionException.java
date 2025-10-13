package org.greenloop.circularfashion.exception;

import org.greenloop.circularfashion.entity.Item;

public class InvalidStatusTransitionException extends RuntimeException {
    
    public InvalidStatusTransitionException(Item.ItemStatus from, Item.ItemStatus to) {
        super(String.format("Invalid status transition from %s to %s", from, to));
    }
    
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}










