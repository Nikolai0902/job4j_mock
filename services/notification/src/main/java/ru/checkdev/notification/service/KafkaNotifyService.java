package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;
import org.springframework.kafka.annotation.KafkaListener;

@Service
@AllArgsConstructor
public class KafkaNotifyService {

    private final TemplateService templates;

    @KafkaListener(topics = "notify")
    public void send(Notify notify) {
        this.templates.send(notify);
    }
}
