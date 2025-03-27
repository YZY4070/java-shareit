package ru.practicum.shareit.item.comments;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class CommentRequestDto {
    private String text;
}
