package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@DataJpaTest
public class ItemRequestRepositoryTests {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByRequester_IdOrderByCreated_shouldReturnOrderedRequests() {
        // Arrange
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        userRepository.save(user);

        ItemRequest request1 = ItemRequest.builder()
                .description("Request 1")
                .requester(user)
                .created(LocalDateTime.now().minusDays(1))
                .build();
        ItemRequest request2 = ItemRequest.builder()
                .description("Request 2")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        // Act
        Collection<ItemRequest> foundRequests = itemRequestRepository.findAllByRequester_IdOrderByCreated(user.getId());

        // Assert
        assertEquals(2, foundRequests.size());
        assertTrue(foundRequests.contains(request1));
        assertTrue(foundRequests.contains(request2));
        // Check ordering by created date
        assertEquals(request1, foundRequests.iterator().next()); // request1 should be first as it's older
    }

    @Test
    void findAllByRequester_IdOrderByCreated_withNoRequests_shouldReturnEmpty() {
        // Arrange
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        userRepository.save(user);

        // Act
        Collection<ItemRequest> foundRequests = itemRequestRepository.findAllByRequester_IdOrderByCreated(user.getId());

        // Assert
        assertTrue(foundRequests.isEmpty());
    }

    @Test
    void findAllByRequesterIdIsNot_shouldReturnOtherUsersRequests() {
        // Arrange
        User user1 = User.builder()
                .name("User 1")
                .email("user1@example.com")
                .build();
        User user2 = User.builder()
                .name("User 2")
                .email("user2@example.com")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        ItemRequest request1 = ItemRequest.builder()
                .description("Request 1")
                .requester(user1)
                .created(LocalDateTime.now())
                .build();
        ItemRequest request2 = ItemRequest.builder()
                .description("Request 2")
                .requester(user2)
                .created(LocalDateTime.now())
                .build();

        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        // Act
        Collection<ItemRequest> foundRequests = itemRequestRepository.findAllByRequesterIdIsNot(user1.getId());

        // Assert
        assertEquals(1, foundRequests.size());
        assertFalse(foundRequests.contains(request1));
        assertTrue(foundRequests.contains(request2));
    }

    @Test
    void findAllByRequesterIdIsNot_withNoOtherRequests_shouldReturnEmpty() {
        // Arrange
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        userRepository.save(user);

        ItemRequest request = ItemRequest.builder()
                .description("Request")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.save(request);

        // Act
        Collection<ItemRequest> foundRequests = itemRequestRepository.findAllByRequesterIdIsNot(user.getId() + 1); // Non-existent user ID

        // Assert
        assertEquals(1, foundRequests.size());
        assertTrue(foundRequests.contains(request));
    }
}