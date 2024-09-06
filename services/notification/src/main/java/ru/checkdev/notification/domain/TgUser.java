package ru.checkdev.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Buslaev
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tg_user")
public class TgUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String email;

    private Boolean subscription = false;

    @Column(name = "chat_id")
    private String chatId;
}
