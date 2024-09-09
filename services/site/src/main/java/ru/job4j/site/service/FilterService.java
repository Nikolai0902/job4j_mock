package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.FilterDTO;

@Service
public class FilterService {

    private final String urlMock;

    public FilterService(@Value("${service.mock}") String urlMock) {
        this.urlMock = urlMock;
    }

    public FilterDTO save(String token, FilterDTO filter) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(urlMock + "/filter/").post(
                token,
                mapper.writeValueAsString(filter)
        );
        return mapper.readValue(out, FilterDTO.class);
    }

    public FilterDTO getByUserId(String token, int userId) throws JsonProcessingException {
        var text = new RestAuthCall(urlMock + "/filter/" + userId)
                .get(token);
        return new ObjectMapper().readValue(text, new TypeReference<>() {
        });
    }

    public void deleteByUserId(String token, int userId) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall(urlMock + "/filter/" + userId).delete(
                token,
                mapper.writeValueAsString(userId)
        );
    }
}
