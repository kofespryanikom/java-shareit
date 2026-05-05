package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody RequestDto requestDto,
                                                @Positive(message = "id пользователя должен быть положительным")
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Post запрос на создание запроса на предмет от пользователя с userId = {}", userId);
        return requestClient.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@Positive(message = "id пользователя должен быть положительным")
                                                  @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get запрос на получение запросов на предмет пользователя с userId = {}", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsExceptUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get запрос на получение всех запросов на предметы кроме запроса от пользователя с userId = {}",
                userId);
        return requestClient.getAllRequestsExceptUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId) {
        log.info("Get запрос на получение запроса на предмет с requestId = {}", requestId);
        return requestClient.getRequestById(requestId);
    }
}
