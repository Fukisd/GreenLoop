package org.greenloop.circularfashion.entity.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.greenloop.circularfashion.entity.Item;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStatusUpdateRequest {
    
    @NotNull(message = "New status is required")
    private Item.ItemStatus newStatus;
    
    private String reason;
}










