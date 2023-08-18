package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comments")
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "item", nullable = false)
    private Long item;

    @Column(name = "author", nullable = false)
    private Long author;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    public Comment(String text, Long item, Long author, LocalDateTime created) {
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }
}
