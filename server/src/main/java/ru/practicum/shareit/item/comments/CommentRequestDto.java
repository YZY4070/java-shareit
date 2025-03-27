package ru.practicum.shareit.item.comments;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CommentRequestDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
