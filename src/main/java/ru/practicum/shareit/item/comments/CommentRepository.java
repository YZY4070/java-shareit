package ru.practicum.shareit.item.comments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> searchCommentsByItemId(Long itemId);

    Collection<Comment> searchCommentByItemIdIn(Collection<Long> itemIds);
}
