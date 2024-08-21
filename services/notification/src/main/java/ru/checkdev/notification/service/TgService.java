package ru.checkdev.notification.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.TgUser;
import ru.checkdev.notification.repository.TgUserRepository;

import java.util.Optional;

/**
 * @author Buslaev
 */
@Service
@AllArgsConstructor
public class TgService {

    private final TgUserRepository repository;

    public TgUser save(TgUser tgUser) {
        return repository.save(tgUser);
    }

    public Optional<TgUser> findByChatId(String chatId) {
        return this.repository.findByChatId(chatId);
    }
}
