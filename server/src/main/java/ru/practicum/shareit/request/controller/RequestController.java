package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithNoIdWithResponsesDto;
import ru.practicum.shareit.request.dto.RequestWithResponsesDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public Request createRequest(@RequestBody RequestDto requestDto, @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<RequestWithNoIdWithResponsesDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<Request> getAllRequestsExceptUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getAllRequestsExceptUser(userId);
    }

    @GetMapping("/{requestId}")
    public RequestWithResponsesDto getRequestWithResponsesById(@PathVariable Long requestId) {
        return requestService.getRequestWithResponsesById(requestId);
    }
}
