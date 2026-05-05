package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    String FIND_REQUESTS_EXCEPT_FROM_USER_QUERY = "SELECT r FROM Request r WHERE r.id != ?1";

    List<Request> findByRequestor_Id(Long userId);

    @Query(FIND_REQUESTS_EXCEPT_FROM_USER_QUERY)
    List<Request> findAllRequestsExceptUser(Long userId);
}
