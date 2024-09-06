package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@AllArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, Notify> kafkaTemplate;

    public void put(final Notify notify) {
        kafkaTemplate.send("notify", notify);
    }
}
