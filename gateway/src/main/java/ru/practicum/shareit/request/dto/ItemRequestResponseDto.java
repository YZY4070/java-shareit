package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForRequestorResponseDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemForRequestorResponseDto> items;
}
