package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class ItemRequestServiceTests {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private User anotherUser;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@test.com");
        userRepository.save(user);

        anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@test.com");
        userRepository.save(anotherUser);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void create_shouldCreateItemRequest() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a hammer");

        ItemRequestResponseDto result = itemRequestService.create(user.getId(), createDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Need a hammer", result.getDescription());
        assertNotNull(result.getCreated());
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotExists() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a hammer");

        assertThrows(NotFoundException.class, () ->
                itemRequestService.create(999L, createDto));
    }

    @Test
    void findById_shouldReturnItemRequestWithItems() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(anotherUser);
        item.setRequestId(itemRequest.getId());
        itemRepository.save(item);

        ItemRequestResponseDto result = itemRequestService.findById(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Drill", result.getItems().iterator().next().getName());
    }

    @Test
    void findById_shouldThrowNotFoundException_whenRequestNotExists() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.findById(999L));
    }


    @Test
    void findAllByUser_shouldReturnUsersRequestsWithItems() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(anotherUser);
        item.setRequestId(itemRequest.getId());
        itemRepository.save(item);

        Collection<ItemRequestResponseDto> result = itemRequestService.findAllByUser(user.getId());

        assertEquals(1, result.size());
        ItemRequestResponseDto response = result.iterator().next();
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals("Need a drill", response.getDescription());
        assertEquals(1, response.getItems().size());
        assertEquals("Drill", response.getItems().iterator().next().getName());
    }

    @Test
    void findAllByUser_shouldThrowNotFoundException_whenUserNotExists() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.findAllByUser(999L));
    }
}