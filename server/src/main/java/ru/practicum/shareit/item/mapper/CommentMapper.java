package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserService userService;

    public Comment toComment(Long owner, Item item, CommentDto commentDto) {
        return new Comment(null, commentDto.getText(), item,
                userService.getUserById(owner));
    }

    public CommentOutDto toCommentOutDto(Comment comment, Long itemId, String authorName) {
        return new CommentOutDto(comment.getId(), comment.getText(), itemId, authorName, comment.getCreated());
    }
}
