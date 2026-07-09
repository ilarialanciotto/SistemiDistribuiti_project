package org.ilaria.progettosistemidistribuiti.Service.AI;

import org.ilaria.progettosistemidistribuiti.Model.DTO.AIAnalysisResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class LocalNlpAnalyzer {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${nlp.service.url}")
    private final String NLP_SERVICE_URL = "http://localhost:5000/api/nlp/analyze";

    public AIAnalysisResultDTO analyze(String description) {
        Map<String, String> request = Map.of("description", description);

        try {
            Map<String, Object> response = restTemplate.postForObject(NLP_SERVICE_URL, request, Map.class);

            String keywords = (String) response.get("keywords");
            String category = (String) response.get("category");
            int priorityLevel = (int) response.get("priority_level");

            return new AIAnalysisResultDTO(keywords, category, Integer.toString(priorityLevel));
        } catch (Exception e) {
            return new AIAnalysisResultDTO("", "other", Integer.toString(3));
        }
    }
}