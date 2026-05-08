package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = new ItemDto(
            "Дрель",
            "Простая дрель",
            true,
            null
    );

    @Test
    public void createItem() throws Exception {
        when(itemClient.createItem(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    public void createInvalidItem() throws Exception {
        ItemDto invalidItemDto = new ItemDto(
                "",
                "Простая дрель",
                true,
                null
        );

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(invalidItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patchItem() throws Exception {
        ItemPatchDto patchDto = new ItemPatchDto();

        when(itemClient.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(patchDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    public void patchItemByInvalidUser() throws Exception {
        ItemPatchDto patchDto = new ItemPatchDto();

        when(itemClient.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", -1)
                        .content(mapper.writeValueAsString(patchDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    public void getItemById() throws Exception {
        when(itemClient.getItemById(anyLong()))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    public void getItemByInvalidId() throws Exception {
        when(itemClient.getItemById(anyLong()))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mvc.perform(get("/items/{itemId}", -1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllItemsByUser() throws Exception {
        List<ItemDto> items = List.of(
                itemDto,
                new ItemDto("Отвертка", "Крестовая", true, null)
        );

        when(itemClient.findItemsByOwner(anyLong()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name", is("Отвертка")));
    }

    @Test
    public void getAllItemsByInvalidUser() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchItems() throws Exception {
        List<ItemDto> items = List.of(itemDto);
        when(itemClient.searchItemsByNameOrDescription(any()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));

        mvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    @Test
    public void createComment() throws Exception {
        CommentDto commentDto = new CommentDto("text");

        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(new ResponseEntity<>(commentDto, HttpStatus.OK));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    public void createCommentByInvalidUserId() throws Exception {
        CommentDto commentDto = new CommentDto("text");

        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(new ResponseEntity<>(commentDto, HttpStatus.OK));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", -1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}