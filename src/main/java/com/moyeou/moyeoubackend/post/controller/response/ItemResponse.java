package com.moyeou.moyeoubackend.post.controller.response;

import com.moyeou.moyeoubackend.post.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Long itemId;
    private String itemName;

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName());
    }
}
