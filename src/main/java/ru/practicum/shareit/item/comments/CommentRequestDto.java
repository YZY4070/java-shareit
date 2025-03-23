package ru.practicum.shareit.item.comments;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
