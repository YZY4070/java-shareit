package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Transactional
@DataJpaTest
public class CommentRepositoryTests {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    private Comment comment;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        User author = User.builder()
                .name("John Wickus")
                .email("johnwickus@test.com")
                .build();
        userRepository.save(author);


        itemRequest = ItemRequest.builder()
                .description("Request for testing")
                .requester(author)
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);


        item = Item.builder()
                .name("Test Item")
                .description("Test description")
                .available(true)
                .owner(author)
                .requestId(itemRequest.getId())
                .build();
        itemRepository.save(item);


        comment = Comment.builder()
                .text("test comment")
                .itemId(item.getId())
                .author(author)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    @Test
    void findByItemId_shouldReturnComment() {
        Collection<Comment> foundComments = commentRepository.searchCommentsByItemId(item.getId());

        assertEquals(1, foundComments.size());
        assertTrue(foundComments.contains(comment));
    }
}
