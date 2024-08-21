package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.TgUser;
import ru.checkdev.notification.dto.PersonDTO;
import ru.checkdev.notification.service.TgService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Arrays;

/**
 * 3. Мидл
 * Класс реализует вывод воод логин и пароль,
 * чтобы привязать аккаунт telegram к платформу CheckDev.
 *
 * @author Buslaev
 */
@AllArgsConstructor
@Slf4j
public class BindAction implements Action {
    private static final String FIND_PERSON_EMAIL_PASS = "/person/check?email=%s&password=%s";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final TgAuthCallWebClint authCallWebClint;
    private final TgService tgService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var sl = System.lineSeparator();
        var chatId = message.getChatId().toString();
        var tg = tgService.findByChatId(chatId);
        if (tg.isPresent()) {
            if (tg.get().getSubscription())
                return new SendMessage(chatId,
                        "Данный аккаунт Telegram уже подписан к платформу CheckDev." + sl
                                + "Чтобы отписаться пройдите по: " + sl
                                + "/unbind"
            );
        }
        var text = "Введите логин и пароль чтобы првязать аккаунт telegram к платформу CheckDev:";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var loginAndPass = Arrays.stream(message.getText().split(" ")).toList();
        var login = loginAndPass.get(0);
        var password = loginAndPass.get(1);
        var text = "";
        var sl = System.lineSeparator();

        if (!tgConfig.isEmail(login)) {
            text = "Login: " + login + " не корректный." + sl
                    + "попробуйте снова." + sl
                    + "/bind";
            return new SendMessage(chatId, text);
        }

        PersonDTO result;
        try {
            result = authCallWebClint.doGet(FIND_PERSON_EMAIL_PASS.formatted(login, password)).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }

        if (result.getEmail() == null) {
            text = "Login или пароль не корректный." + sl
                    + "Попробуйте снова." + sl
                    + "/bind";
            return new SendMessage(chatId, text);
        }

        TgUser tgUser = tgService.findByChatId(chatId).get();
        tgUser.setSubscription(true);
        tgService.save(tgUser);

        text = "Аккаунт telegram првязан к платформе CheckDev.";
        return new SendMessage(chatId, text);
    }
}
