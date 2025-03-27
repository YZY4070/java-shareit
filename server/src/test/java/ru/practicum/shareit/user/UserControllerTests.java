package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Alice Smith")
                .email("alice.smith@test.com")
                .build();
    }

    @Test
    void findById_shouldReturnUser() throws Exception {
        when(userService.findById(1L))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice.smith@test.com"));
    }

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        UserDto createDto = UserDto.builder()
                .name("Bob Johnson")
                .email("bob.johnson@test.com")
                .build();

        UserDto createdResult = UserDto.builder()
                .id(1L)
                .name("Bob Johnson")
                .email("bob.johnson@test.com")
                .build();

        when(userService.save(any(UserDto.class)))
                .thenReturn(createdResult);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Bob Johnson"))
                .andExpect(jsonPath("$.email").value("bob.johnson@test.com"));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("Charlie Brown")
                .email("charlie.brown@test.com")
                .build();

        UserDto updatedResult = UserDto.builder()
                .id(1L)
                .name("Charlie Brown")
                .email("charlie.brown@test.com")
                .build();

        when(userService.save(any(UserDto.class)))
                .thenReturn(updatedResult);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Charlie Brown"))
                .andExpect(jsonPath("$.email").value("charlie.brown@test.com"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}