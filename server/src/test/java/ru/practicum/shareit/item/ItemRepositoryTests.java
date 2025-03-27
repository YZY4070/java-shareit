package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@DataJpaTest
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private Item item;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("rofls")
                .email("rofler@test.com")
                .build();
        User owner2 = User.builder()
                .name("muteallchat")
                .email("valvezachto@test.com")
                .build();
        userRepository.save(owner);
        userRepository.save(owner2);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Need a laptop for work")
                .requester(owner)
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("need rofls")
                .requester(owner2)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest2);

        item = Item.builder()
                .name("Test Item")
                .description("Test description")
                .available(true)
                .owner(owner)
                .requestId(itemRequest.getId())
                .build();
        item2 = Item.builder()
                .name("Test Item 2")
                .description("Test description 2")
                .available(true)
                .owner(owner2)
                .requestId(itemRequest2.getId())
                .build();
        itemRepository.save(item);
        itemRepository.save(item2);
    }


    @Test
    void findItemsByOwnerId() {
        Collection<Item> foundItems = itemRepository.findItemsByOwnerId(owner.getId());

        assertFalse(foundItems.isEmpty());
        assertEquals(1, foundItems.size());
        assertTrue(foundItems.contains(item));
    }

    @Test
    void findItemByText() {
        Collection<Item> foundItems = itemRepository.findItemByText("Test description");

        assertFalse(foundItems.isEmpty());
        assertEquals(2, foundItems.size());
        assertTrue(foundItems.contains(item));
        assertTrue(foundItems.contains(item2));
    }

    @Test
    void findItemByTextWithNoMatches() {
        Collection<Item> foundItems = itemRepository.findItemByText("Non-existent text");

        assertTrue(foundItems.isEmpty());
    }

    @Test
    void findAllByRequestIdIn() {
        Collection<Item> foundItems = itemRepository.findAllByRequestIdIn(List.of(1L));

        System.out.println("Found items: " + foundItems.size());
        foundItems.forEach(item -> System.out.println("Item: " + item));

        assertEquals(1, foundItems.size());
        assertTrue(foundItems.contains(item));
    }


    @Test
    void findAllByRequestId() {
        Collection<Item> foundItems = itemRepository.findAllByRequestId(2L);

        assertEquals(1, foundItems.size());
        assertTrue(foundItems.contains(item2));
    }
}
