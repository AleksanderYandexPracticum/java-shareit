package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "items")
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description; //развёрнутое описание

    @Column(name = "is_available", nullable = false)
    private Boolean available;  //статус о том, доступна или нет вещь для аренды

    @Column(name = "owner_id", nullable = false)
    private Long owner;  //владелец вещи

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    @Column(name = "request_id")
    private Long request;  //если вещь была создана по запросу другого пользователя, то в этом
    //поле будет храниться ссылка на соответствующий запрос

    public Item(String name, String description, Boolean available, Long owner, Long request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
