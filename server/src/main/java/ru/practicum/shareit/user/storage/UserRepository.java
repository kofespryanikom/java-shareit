package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    String FIND_ALL_QUERY = "SELECT u.email FROM User u";

    @Query(FIND_ALL_QUERY)
    Set<String> findAllEmails();
}
