package org.ilaria.progettosistemidistribuiti.Service.AI;

import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AIAnalysisResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.*;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Analyzer {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${nlp.service.url}")
    private String NLP_SERVICE_URL;
    private final ComprehendClient comprehendClient;

    public AIAnalysisResultDTO analyzeTicket(String description) {
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

    public AIAnalysisResultDTO analyze(String description) {
        int priorityLevel = 3;
        String keywords = "";
        String category = "other";
        Map<String, String> request = Map.of("description", description);
        try {
            DetectSentimentRequest sentimentRequest = DetectSentimentRequest.builder()
                    .text(description)
                    .languageCode(LanguageCode.IT)
                    .build();
            DetectSentimentResponse sentimentResponse = comprehendClient.detectSentiment(sentimentRequest);
            String sentiment = sentimentResponse.sentimentAsString();

            if ("NEGATIVE".equals(sentiment)) {
                priorityLevel = 5;
            } else if ("MIXED".equals(sentiment)) {
                priorityLevel = 4;
            } else if ("NEUTRAL".equals(sentiment)) {
                priorityLevel = 2;
            } else if ("POSITIVE".equals(sentiment)) {
                priorityLevel = 1;
            }
        } catch (Exception e) {
            System.err.println("Errore durante il rilevamento del Sentiment: " + e.getMessage());
        }
        try {
            DetectKeyPhrasesRequest keyPhrasesRequest = DetectKeyPhrasesRequest.builder()
                    .text(description)
                    .languageCode(LanguageCode.IT)
                    .build();
            DetectKeyPhrasesResponse keyPhrasesResponse = comprehendClient.detectKeyPhrases(keyPhrasesRequest);

            keywords = keyPhrasesResponse.keyPhrases().stream()
                    .map(KeyPhrase::text)
                    .flatMap(phrase -> java.util.Arrays.stream(phrase.split("\\s+")))
                    .map(word -> word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase())
                    .filter(word -> word.length() > 3)
                    .distinct()
                    .limit(5)
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            System.err.println("Errore durante l'estrazione delle Keyphrases: " + e.getMessage());
        }
        try {
            Map<String, Object> response = restTemplate.postForObject(NLP_SERVICE_URL, request, Map.class);

            category = (String) response.get("category");

        } catch (Exception e) {
            System.err.println("Errore di connessione con il microservizio spaCy: " + e.getMessage());
        }
        return new AIAnalysisResultDTO(keywords, category, Integer.toString(priorityLevel));
    }
}