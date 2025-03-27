package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemCommentsBookingDto itemCommentsBookingDto;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .authorName("John Wick")
                .created(LocalDateTime.of(2025, 3, 13, 13, 0, 0))
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        itemCommentsBookingDto = ItemCommentsBookingDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .comments(List.of(commentDto))
                .build();

        User booker = User.builder()
                .id(1L)
                .name("John Snow")
                .email("johnsnow@test.com")
                .build();
    }

    @Test
    void findById_shouldReturnItemCommentsBookingDto() throws Exception {
        when(itemService.findById(any(Long.class)))
                .thenReturn(itemCommentsBookingDto);

        mockMvc.perform(get("/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemCommentsBookingDto)));
    }

    @Test
    void findAllByOwnerId_shouldReturnItemDtoCollection() throws Exception {
        when(itemService.findItemsByOwnerId(any(Long.class)))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void findItemsByText_shouldReturnItemDtoCollection() throws Exception {
        when(itemService.findItemByText(any(String.class)))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void create_shouldReturnItemDto() throws Exception {
        when(itemService.addItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void update_shouldReturnUpdatedItemDto() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        itemDto.setId(1L);
        mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

}
