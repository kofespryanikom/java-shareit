package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTests {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private MockMvc mvc;

    private final RequestDto requestDto = new RequestDto(
            "Описание"
    );

    @Test
    public void createRequest() throws Exception {
        when(requestClient.createRequest(any(), anyLong()))
                .thenReturn(new ResponseEntity<>(requestDto, HttpStatus.OK));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    public void createInvalidRequest() throws Exception {
        RequestDto invalidRequestDto = new RequestDto(
                null
        );

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(invalidRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getOwnRequests() throws Exception {
        List<RequestDto> requests = List.of(requestDto);

        when(requestClient.getUserRequests(anyLong()))
                .thenReturn(new ResponseEntity<>(requests, HttpStatus.OK));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())));
    }

    @Test
    public void getOwnRequestsByInvalidUser() throws Exception {
        List<RequestDto> requests = List.of(requestDto);

        when(requestClient.getUserRequests(anyLong()))
                .thenReturn(new ResponseEntity<>(requests, HttpStatus.OK));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllRequests() throws Exception {
        List<RequestDto> requests = List.of(requestDto);

        when(requestClient.getAllRequestsExceptUser(anyLong()))
                .thenReturn(new ResponseEntity<>(requests, HttpStatus.OK));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())));
    }

    @Test
    public void getRequestById() throws Exception {
        when(requestClient.getRequestById(anyLong()))
                .thenReturn(new ResponseEntity<>(requestDto, HttpStatus.OK));

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }
}