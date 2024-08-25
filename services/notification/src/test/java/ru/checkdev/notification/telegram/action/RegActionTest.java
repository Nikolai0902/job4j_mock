package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.service.TgService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class RegActionTest {

    @Mock
    private TgAuthCallWebClint authCallWebClint;
    @Mock
    private TgService tgService;
    @Mock
    TgConfig tgConfig;

    @InjectMocks
    private RegAction regAction;

    private final long chatId = 0;
    private final String chatIdString = String.valueOf("0");

    @Test
    void whenRegSuccess() {
        var fio = "username";
        var email = "email@m.ru";
        var password = "password";
        var sl = System.lineSeparator();

        var message = new Message();
        message.setText(fio + " " + email);
        var chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);

        when(tgConfig.parseMessageFioAndEmail(message)).thenReturn(Map.of(
                "username", fio,
                "email", email
        ));
        when(tgConfig.getPassword()).thenReturn(password);
        when(tgConfig.isEmail(any())).thenReturn(true);
        when(tgConfig.getObjectToMap(any())).thenReturn(Map.of("username", "username"));
        when(authCallWebClint.doPost(any(), any())).thenReturn(Mono.just(new Object()));

        var text = "Вы зарегистрированы: " + sl
                + "Логин: " + email + sl
                + "Пароль: " + password + sl
                + null;
        SendMessage rsl = (SendMessage) regAction.callback(message);
        assertThat(rsl)
                .isEqualTo(new SendMessage(chatIdString, text));
    }

}