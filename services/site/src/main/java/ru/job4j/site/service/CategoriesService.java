package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.TopicDTO;

import java.util.List;

@Service
public class CategoriesService {

    private final String urlDesc;
    private final TopicsService topicsService;
    private final InterviewsService interviewsService;

    public CategoriesService(@Value("${service.desc}") String urlDesc, TopicsService topicsService, InterviewsService interviewsService) {
        this.urlDesc = urlDesc;
        this.topicsService = topicsService;
        this.interviewsService = interviewsService;
    }

    public List<CategoryDTO> getAll() throws JsonProcessingException {
        var text = new RestAuthCall(urlDesc + "/categories/").get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public List<CategoryDTO> getPopularFromDesc() throws JsonProcessingException {
        var text = new RestAuthCall(urlDesc + "/categories/most_pop").get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public CategoryDTO create(String token, CategoryDTO category) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(urlDesc + "/category/").post(
                token,
                mapper.writeValueAsString(category)
        );
        return mapper.readValue(out, CategoryDTO.class);
    }

    public void update(String token, CategoryDTO category) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall(urlDesc + "/category/").put(
                token,
                mapper.writeValueAsString(category)
        );
    }

    public void updateStatistic(String token, int categoryId) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall(urlDesc + "/category/statistic").put(
                token, mapper.writeValueAsString(categoryId));
    }

    public List<CategoryDTO> getAllWithTopics() throws JsonProcessingException {
        var categoriesDTO = getAll();
        for (var categoryDTO : categoriesDTO) {
            categoryDTO.setTopicsSize(topicsService.getByCategory(categoryDTO.getId()).size());
        }
        return categoriesDTO;
    }

    public List<CategoryDTO> getMostPopular() throws JsonProcessingException {
        var categoriesDTO = getPopularFromDesc();
        for (var categoryDTO : categoriesDTO) {
            categoryDTO.setTopicsSize(topicsService.getByCategory(categoryDTO.getId()).size());
            long number = 0;
            for (TopicDTO topicDTO : topicsService.getByCategory(categoryDTO.getId())) {
                Page<InterviewDTO> byTopicId = interviewsService.getByTopicId(topicDTO.getId(), 1, 1);
                number += byTopicId.getTotalElements();
            }
            categoryDTO.setInterviewNumber((int) number);
        }
        return categoriesDTO;
    }

    public String getNameById(List<CategoryDTO> list, int id) {
        String result = "";
        for (CategoryDTO category : list) {
            if (id == category.getId()) {
                result = category.getName();
                break;
            }
        }
        return result;
    }
}
