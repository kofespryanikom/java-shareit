package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:file:./db/filmorate",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class RequestServiceImplTests {

    @Autowired
    private EntityManager em;

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createRequestSavesRequestToDb() {
        User user = createAndSaveUser("requestor@email.com", "Requestor");
        RequestDto dto = makeRequestDto("Нужна отвертка", user);

        Request saved = requestService.createRequest(dto, user.getId());

        TypedQuery<Request> query = em.createQuery(
                "select r from Request r where r.description = :desc and r.requestor.id = :requestorId",
                Request.class
        );
        Request found = query.setParameter("desc", dto.getDescription())
                .setParameter("requestorId", user.getId())
                .getSingleResult();

        assertThat(found.getId(), notNullValue());
        assertThat(found.getDescription(), equalTo(dto.getDescription()));
        assertThat(found.getRequestor(), equalTo(user));
        assertThat(found.getCreated(), notNullValue());

        assertThat(saved.getId(), equalTo(found.getId()));
        assertThat(saved.getDescription(), equalTo(found.getDescription()));
    }

    @Test
    void getUserRequestsReturnsRequestsForUser() {
        User user = createAndSaveUser("tester@email.com", "Юзер");
        RequestDto dto1 = makeRequestDto("Дрель нужна", user);
        RequestDto dto2 = makeRequestDto("Книги на полку", user);
        requestService.createRequest(dto1, user.getId());
        requestService.createRequest(dto2, user.getId());

        var result = requestService.getUserRequests(user.getId());

        assertThat(result, hasSize(2));
        assertThat(result.toString(), allOf(containsString("Дрель нужна"), containsString("Книги на полку")));
    }

    @Test
    void getRequestByIdFindsRequest() {
        User user = createAndSaveUser("abc@email.com", "Имя");
        RequestDto dto = makeRequestDto("Молоток", user);
        Request saved = requestService.createRequest(dto, user.getId());

        Request found = requestService.getRequestById(saved.getId());

        assertThat(found, notNullValue());
        assertThat(found.getId(), equalTo(saved.getId()));
        assertThat(found.getDescription(), equalTo("Молоток"));
        assertThat(found.getRequestor().getId(), equalTo(user.getId()));
    }

    @Test
    void getAllRequestsExceptUserExcludesRequestsByUser() {
        User user1 = createAndSaveUser("first@email.com", "One");
        User user2 = createAndSaveUser("second@email.com", "Two");
        requestService.createRequest(makeRequestDto("Сумка", user1), user1.getId());
        requestService.createRequest(makeRequestDto("Кресло", user2), user2.getId());

        var result = requestService.getAllRequestsExceptUser(user1.getId());
        assertThat(result, hasSize(2));
        assertThat(result.toString(), containsString("Кресло"));
    }

    private User createAndSaveUser(String email, String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private RequestDto makeRequestDto(String description, User requestor) {
        return new RequestDto(description, requestor, LocalDateTime.now());
    }
}
