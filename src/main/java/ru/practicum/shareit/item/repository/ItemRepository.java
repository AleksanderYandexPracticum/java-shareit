package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Item save(Item item);

    Item getById(Long id);

    Item findItemById(Long id);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String text1, String text2);

    List<Item> findItemsByOwner(Long owner);

    Item getItemByIdAndOwner(Long id, Long owner);

    List<Item> getItemsByRequestIdIn(List<Long> requestIds);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String text1, String text2, Pageable pageable);

    List<Item> findItemsByOwner(Long owner, Pageable pageable);
}
