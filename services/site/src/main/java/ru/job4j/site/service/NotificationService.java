package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.SubscribeCategory;
import ru.job4j.site.dto.SubscribeTopicDTO;
import ru.job4j.site.dto.UserDTO;
import ru.job4j.site.dto.UserTopicDTO;

import java.util.List;

@Service
public class NotificationService {

    private final String urlNotification;

    public NotificationService(@Value("${service.notification}") String urlNotification) {
        this.urlNotification = urlNotification;
    }

    public void addSubscribeCategory(String token, int userId, int categoryId) throws JsonProcessingException {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(urlNotification + "/subscribeCategory/add").post(
                token, mapper.writeValueAsString(subscribeCategory));
    }

    public void deleteSubscribeCategory(String token, int userId, int categoryId) throws JsonProcessingException {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(urlNotification + "/subscribeCategory/delete").post(
                token, mapper.writeValueAsString(subscribeCategory));
    }

    public UserDTO findCategoriesByUserId(int id) throws JsonProcessingException {
        var text = new RestAuthCall(urlNotification + "/subscribeCategory/" + id).get();
        var mapper = new ObjectMapper();
        List<Integer> list = mapper.readValue(text, new TypeReference<>() {
        });
        return new UserDTO(id, list);
    }

    public void addSubscribeTopic(String token, int userId, int topicId) throws JsonProcessingException {
        SubscribeTopicDTO subscribeTopicDTO = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(urlNotification + "/subscribeTopic/add").post(
                token, mapper.writeValueAsString(subscribeTopicDTO));
    }

    public void deleteSubscribeTopic(String token, int userId, int topicId) throws JsonProcessingException {
        SubscribeTopicDTO subscribeTopic = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(urlNotification + "/subscribeTopic/delete").post(
                token, mapper.writeValueAsString(subscribeTopic));
    }

    public UserTopicDTO findTopicByUserId(int id) throws JsonProcessingException {
        var text = new RestAuthCall(urlNotification + "/subscribeTopic/" + id).get();
        var mapper = new ObjectMapper();
        List<Integer> list = mapper.readValue(text, new TypeReference<>() {
        });
        return new UserTopicDTO(id, list);
    }
}