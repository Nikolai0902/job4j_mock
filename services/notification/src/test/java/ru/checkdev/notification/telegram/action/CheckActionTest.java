package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.checkdev.notification.domain.TgUser;
import ru.checkdev.notification.service.TgService;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckActionTest {

    @Mock
    private TgService tgService;

    @InjectMocks
    private CheckAction checkAction;

    private final long chatId = 12345;
    private final String chatIdString = String.valueOf("12345");


    @Test
    public void whenAccountNotExists() {
        var sl = System.lineSeparator();
        var response = "Данный аккаунт Telegram не зарегистрирован." + sl
                + "Для регистрации передите по: " + sl
                + "/new";
        Message message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(tgService.findByChatId(chatIdString)).thenReturn(Optional.empty());

        assertThat(checkAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenAccountExists() {
        var sl = System.lineSeparator();
        var username = "username";
        var email = "123@mail.ru";

        var text = "ФИО: " + username + sl
                + "Email: " + email;
        var message = mock(Message.class);
        var chatIdObj = new TgUser(1, username, email, true, chatIdString);

        when(message.getChatId()).thenReturn(chatId);
        when(tgService.findByChatId(chatIdString)).thenReturn(Optional.of(chatIdObj));

        assertThat(checkAction.callback(message)).isEqualTo(new SendMessage(chatIdString, text));
    }
}