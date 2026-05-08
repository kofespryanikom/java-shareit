package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    String FIND_ITEMS_BY_SEARCH_QUERY = "SELECT i " +
                                        "FROM Item i " +
                                        "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
                                           "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) " +
                                          "AND i.available = true";
    String FIND_ITEMS_BY_REQUESTS_QUERY = "SELECT i FROM Item i WHERE i.request.id IN (?1)";

    List<Item> findByOwner_Id(Long userId);

    @Query(FIND_ITEMS_BY_SEARCH_QUERY)
    List<Item> searchItems(String text);

    @Query(FIND_ITEMS_BY_REQUESTS_QUERY)
    List<Item> getItemsCreatedOnRequests(List<Long> listOfIds);
}
