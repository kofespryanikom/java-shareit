package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithNoIdWithResponsesDto;
import ru.practicum.shareit.request.dto.RequestWithResponsesDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final UserService userService;

    public Request toRequest(Long requestId, RequestDto requestDto, Long userId, LocalDateTime created) {
        return new Request(requestId, requestDto.getDescription(), userService.getUserById(userId), created);
    }

    public RequestWithNoIdWithResponsesDto toRequestWithNoIdWithResponsesDto(Request request) {
        return new RequestWithNoIdWithResponsesDto(request.getDescription(), request.getCreated(),
                new ArrayList<>());
    }

    public RequestWithResponsesDto toRequestWithResponsesDto(Request request) {
        return new RequestWithResponsesDto(request.getId(), request.getDescription(), request.getCreated(),
                new ArrayList<>());
    }
}
