package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:file:./db/filmorate",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class UserServiceImplTests {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserServiceImpl userService;

    @Test
    void createUser() {
        UserDto userDto = makeUserDto("vasya@mail.ru", "Вася");

        User result = userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery(
                "select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void createUserThrowsOnDuplicateEmail() {
        UserDto userDto = makeUserDto("dup@mail.ru", "Коля");
        userService.createUser(userDto);

        UserDto userDto2 = makeUserDto("dup@mail.ru", "Иван");
        assertThrows(ConflictException.class, () -> userService.createUser(userDto2));
    }

    @Test
    void updateUser() {
        UserDto userDto = makeUserDto("test@email.com", "Alex");
        User saved = userService.createUser(userDto);

        UserPatchDto patch = new UserPatchDto();
        patch.setName("UpdatedName");
        patch.setEmail("updated@email.com");
        User updated = userService.updateUser(saved.getId(), patch);

        assertThat(updated.getName(), equalTo("UpdatedName"));
        assertThat(updated.getEmail(), equalTo("updated@email.com"));

        User found = em.find(User.class, saved.getId());
        assertThat(found.getName(), equalTo("UpdatedName"));
        assertThat(found.getEmail(), equalTo("updated@email.com"));
    }

    @Test
    void updateUserThrowsIfEmailExists() {
        UserDto userDto1 = makeUserDto("mail1@mail.com", "First");
        UserDto userDto2 = makeUserDto("mail2@mail.com", "Second");
        User saved1 = userService.createUser(userDto1);
        userService.createUser(userDto2);

        UserPatchDto patch = new UserPatchDto();
        patch.setEmail("mail2@mail.com");

        assertThrows(ConflictException.class, () -> userService.updateUser(saved1.getId(), patch));
    }

    @Test
    void updateUserThrowsIfNameIsBlank() {
        UserDto userDto = makeUserDto("blank@mail.com", "ПравильноеИмя");
        User saved = userService.createUser(userDto);

        UserPatchDto patch = new UserPatchDto();
        patch.setName("    ");

        assertThrows(ValidationException.class, () -> userService.updateUser(saved.getId(), patch));
    }

    @Test
    void getUserByIdReturnsUser() {
        UserDto userDto = makeUserDto("getid@mail.com", "Ирина");
        User saved = userService.createUser(userDto);

        User found = userService.getUserById(saved.getId());

        assertThat(found, notNullValue());
        assertThat(found.getName(), equalTo(userDto.getName()));
        assertThat(found.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUserByIdThrowsIfNotExists() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(9999L));
    }

    @Test
    void deleteUserRemovesUser() {
        UserDto userDto = makeUserDto("del@mail.com", "Тест");
        User saved = userService.createUser(userDto);

        userService.deleteUser(saved.getId());

        TypedQuery<User> query = em.createQuery(
                "select u from User u where u.email = :email", User.class);
        query.setParameter("email", userDto.getEmail());
        assertTrue(query.getResultList().isEmpty());
    }

    private UserDto makeUserDto(String email, String name) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
}
