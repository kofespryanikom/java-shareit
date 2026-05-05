package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithNoIdWithResponsesDto;
import ru.practicum.shareit.request.dto.RequestWithResponsesDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(RequestDto requestDto, Long userId);

    List<RequestWithNoIdWithResponsesDto> getUserRequests(Long userId);

    List<Request> getAllRequestsExceptUser(Long userId);

    Request getRequestById(Long requestId);

    RequestWithResponsesDto getRequestWithResponsesById(Long requestId);
}
