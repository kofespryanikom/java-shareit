package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithNoIdWithResponsesDto;
import ru.practicum.shareit.request.dto.RequestWithResponsesDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Transactional
    public Request createRequest(RequestDto requestDto, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Request requestSaved = requestRepository.save(requestMapper.toRequest(null, requestDto, userId, now));
        log.info("Создан запрос с requestId = {}", requestSaved.getId());

        return requestSaved;
    }

    public List<RequestWithNoIdWithResponsesDto> getUserRequests(Long userId) {
        List<Request> requestsToProcess = requestRepository.findByRequestor_Id(userId);
        List<Long> requestsToProcessIds = requestsToProcess.stream()
                .map(Request::getId)
                .toList();
        Map<Long, RequestWithNoIdWithResponsesDto> requestDtosMap = requestsToProcess.stream()
                .collect(Collectors.toMap(Request::getId, requestMapper::toRequestWithNoIdWithResponsesDto));

        List<Item> itemsCreatedOnRequests = itemRepository.getItemsCreatedOnRequests(requestsToProcessIds);

        for (Item item : itemsCreatedOnRequests) {
            RequestWithNoIdWithResponsesDto requestWithNoIdWithResponsesDto = requestDtosMap.get(item.getRequest()
                    .getId());
            requestWithNoIdWithResponsesDto.getItems().add(itemMapper.toItemNoAvailableDto(item));
        }

        return new ArrayList<>(requestDtosMap.values());
    }

    public List<Request> getAllRequestsExceptUser(Long userId) {
        return requestRepository.findAllRequestsExceptUser(userId);
    }

    public Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseGet(() -> {
            log.warn("Запрос с requestId = {} не найден", requestId);
            throw new NotFoundException("Запрос с requestId = " + requestId + " не найден");
        });
    }

    public RequestWithResponsesDto getRequestWithResponsesById(Long requestId) {
        Request request = getRequestById(requestId);

        RequestWithResponsesDto requestWithResponsesDto = requestMapper.toRequestWithResponsesDto(request);
        List<Item> itemsCreatedOnRequests = itemRepository.getItemsCreatedOnRequests(List.of(requestId));

        for (Item item : itemsCreatedOnRequests) {
            requestWithResponsesDto.getItems().add(itemMapper.toItemNoAvailableDto(item));
        }

        return requestWithResponsesDto;
    }
}
