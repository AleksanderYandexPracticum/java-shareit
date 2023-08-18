package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookings")
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;   //дата и время начала бронирования

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;  //дата и время конца бронирования

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;  //вещь, которую пользователь бронирует

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;  //пользователь, который осуществляет бронирование

    @Enumerated(EnumType.STRING)
    private Status status;  //статус бронирования
    // WAITING — новое бронирование, ожидает одобрения
    // APPROVED — бронирование подтверждено владельцем
    // REJECTED — бронирование отклонено владельцем
    // CANCELED — бронирование отменено создателем

//    @Transient
//    String name;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}