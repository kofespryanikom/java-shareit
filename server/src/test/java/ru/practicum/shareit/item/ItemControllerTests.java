package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDates;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

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
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final Item item = new Item(
            1L,
            "Дрель",
            "Простая дрель",
            true,
            null,
            null
    );

    @Test
    public void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.isAvailable())));
    }

    @Test
    public void patchItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(item);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    public void getItemById() throws Exception {
        when(itemService.findItemWithBookingDatesAndCommentsByItemId(anyLong()))
                .thenReturn(new ItemDtoWithBookingDates(null, "Дрель", null, null,
                        null, null, null, null));

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    public void getAllItemsByUser() throws Exception {
        List<ItemDtoWithBookingDates> items = List.of(
                new ItemDtoWithBookingDates(
                        1L,
                        "Дрель",
                        "Простая дрель",
                        true,
                        null,
                        null, // lastBooking
                        null, // nextBooking
                        List.of() // comments
                ),
                new ItemDtoWithBookingDates(
                        2L,
                        "Отвертка",
                        "Крестовая",
                        true,
                        null,
                        null, // lastBooking
                        null, // nextBooking
                        List.of()
                )
        );

        when(itemService.findItemsByOwner(anyLong()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name", is("Отвертка")));
    }

    @Test
    public void searchItems() throws Exception {
        List<Item> items = List.of(item);
        when(itemService.searchItemsByName(any()))
                .thenReturn(items);

        mvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(item.getName())));
    }

    @Test
    public void createComment() throws Exception {
        CommentDto commentDto = new CommentDto("text");

        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(new CommentOutDto(null, "text", null, null, null));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}