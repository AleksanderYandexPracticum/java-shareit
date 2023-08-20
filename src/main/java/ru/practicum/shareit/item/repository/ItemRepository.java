package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(Long owner);

    Item save(Item item);

    Item getById(Long id);

    Item findItemById(Long id);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String text1, String text2);

    List<Item> findItemsByOwner(Long owner);

    Item findItemByIdAndOwner(Long id, Long owner);


}
