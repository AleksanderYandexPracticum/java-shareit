package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment save(CommentDto commentDto);

    List<Comment> findAllByAuthorAndItem(User author, Item item);

    List<Comment> findAllByItem(Item item);

}
