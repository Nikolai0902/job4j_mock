package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class InfoActionTest {

    private final String chatId = String.valueOf("0");

    @Test
    void whenSendMenu() {
        String sl = System.lineSeparator();
        Message message = mock(Message.class);
        InfoAction infoAction = new InfoAction(List.of("/one", "/two"));
        var result = infoAction.handle(message);
        assertThat(result).isEqualTo(new SendMessage(chatId,
                        "Выберите действие:" + sl
                        + "/one" + sl
                        + "/two" + sl));
    }
}