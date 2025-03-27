package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Хочу отвертку")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Test
    void create_shouldReturnCreatedItemRequest() throws Exception {
        ItemRequestCreateDto requestDto = ItemRequestCreateDto.builder()
                .description("Хочу отвертку")
                .build();

        when(itemRequestService.create(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    void findAllWithoutUser_shouldReturnItemRequests() throws Exception {
        when(itemRequestService.findAll(anyLong()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(responseDto.getId()))
                .andExpect(jsonPath("$[0].description").value(responseDto.getDescription()));
    }

    @Test
    void findAllForUser_shouldReturnItemRequests() throws Exception {
        when(itemRequestService.findAllByUser(anyLong()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(responseDto.getId()))
                .andExpect(jsonPath("$[0].description").value(responseDto.getDescription()));
    }

    @Test
    void findById_shouldReturnItemRequest() throws Exception {
        when(itemRequestService.findById(anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.description").value(responseDto.getDescription()));
    }
}