package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class ItemForRequestorResponseDto {
    private Long id;
    private String name;
}
