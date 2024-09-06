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

import java.util.Calendar;
import java.util.Optional;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@AllArgsConstructor
@Slf4j
public class RegAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String URL_AUTH_REGISTRATION = "/registration";
    private final TgConfig tgConfig;
    private final TgAuthCallWebClint authCallWebClint;
    private final String urlSiteAuth;
    private final TgService tgService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        Optional<TgUser> byChatId = tgService.findByChatId(chatId);
        if (byChatId.isPresent()) {
            return new SendMessage(chatId, "Данный аккаунт Telegram зарегистрирован на сайте.");
        }
        var text = "Введите ФИО и email для регистрации:";
        return new SendMessage(chatId, text);
    }

    /**
     * Метод формирует ответ пользователю.
     * Весь метод разбит на 4 этапа проверки.
     * 1. Проверка на соответствие формату Email введенного текста.
     * 2. Отправка данных в сервис Auth и если сервис не доступен сообщаем
     * 3. Если сервис доступен, получаем от него ответ и обрабатываем его.
     * 3.1 ответ при ошибке регистрации
     * 3.2 ответ при успешной регистрации.
     *
     * @param message Message
     * @return BotApiMethod<Message>
     */
    @Override
    public BotApiMethod<Message> callback(Message message) {
        var text = "";
        var sl = System.lineSeparator();
        var chatId = message.getChatId().toString();
        var fioAndEmail = tgConfig.parseMessageFioAndEmail(message);

        if (fioAndEmail.isEmpty()) {
            text = "Данные некорректны.";
            return new SendMessage(chatId, text);
        }
        var fio = fioAndEmail.get("fio");
        var email = fioAndEmail.get("email");

        if (!tgConfig.isEmail(email)) {
            text = "Email: " + email + " не корректный." + sl
                    + "попробуйте снова." + sl
                    + "/new";
            return new SendMessage(chatId, text);
        }

        var password = tgConfig.getPassword();
        var person = new PersonDTO(email, password, true, null,
                Calendar.getInstance());
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_REGISTRATION, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }

        var mapObject = tgConfig.getObjectToMap(result);

        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка регистрации: "
                    + mapObject.get(ERROR_OBJECT) + sl
                    + "попробуйте снова." + sl
                    + "/new";
            return new SendMessage(chatId, text);
        }

        TgUser newUserTg = new TgUser();
        newUserTg.setChatId(chatId);
        newUserTg.setUsername(fio);
        newUserTg.setEmail(email);
        tgService.save(newUserTg);

        text = "Вы зарегистрированы: " + sl
                + "Логин: " + email + sl
                + "Пароль: " + password + sl
                + urlSiteAuth;
        return new SendMessage(chatId, text);
    }
}
