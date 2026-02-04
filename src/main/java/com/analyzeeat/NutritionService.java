package com.analyzeeat;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;


@Service
public class NutritionService {

	 
	@Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public NutritionService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }


    public Mono<Map<String, Object>> analyzeIngredients(AnalysisRequest request)  {
        String prompt = buildPrompt(request);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        return webClient.post()
                .uri(geminiApiUrl + "?key=" + geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractTextFromResponse)
                .map(this::cleanJsonResponse)
                .map(this::parseToMap);
            
    }
    

	
	private String extractTextFromResponse(String res) {
		 try {
	            GeminiResponse geminiResponse = objectMapper.readValue(res, GeminiResponse.class);
	            if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
	                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
	                if (firstCandidate.getContent() != null &&
	                        firstCandidate.getContent().getParts() != null &&
	                        !firstCandidate.getContent().getParts().isEmpty()) {
	                    return firstCandidate.getContent().getParts().get(0).getText();
	                }
	            }
	            return "No content found in response";
	        } catch (Exception e) {
	            return "Error Parsing: " + e.getMessage();
	        }
	}
	
	private Map<String, Object> parseToMap(String json) {
	    try {
	        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
	    }
	}
	private String cleanJsonResponse(String response) {

	    String cleaned = response.trim();
	    
	    if (cleaned.startsWith("```json")) {
	        cleaned = cleaned.substring(7); // remove ```json
	    } else if (cleaned.startsWith("```")) {
	        cleaned = cleaned.substring(3); // remove ```
	    }
	    
	    if (cleaned.endsWith("```")) {
	        cleaned = cleaned.substring(0, cleaned.length() - 3); // remove ```
	    }
	    
	    return cleaned.trim();
	}

	private String buildPrompt(AnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();
        switch (request.getOperation()) {
            case "NUTRITION":
                prompt.append("""
                		You are a nutrition expert.
        I will provide you with text that may include recipe names, credits, and instructions.
        
        YOUR TASK:
        1. Identify and extract ONLY the food ingredients and their quantities. Ignore everything else.
        2. Calculate values PER SERVING.
        3. Detect the input language and return all text values in that language.
        
        Return ONLY a valid JSON object with these keys (keep keys in English): 
        "detected_ingredients": (a comma-separated string of the ingredients you found),
        "servings": (number),
        "calories_per_serving": (number),
        "protein_per_serving": (number), 
        "carbs_per_serving": (number),
        "fat_per_serving": (number)
                		""");
                break;
            case "HEALTHY_SWAP":
                prompt.append("""
                		You are a Low-Calorie Chef. 
        Identify the ingredients and suggest substitutions to reduce calories WITHOUT reducing portion size.
        
         STRICT RULES:
        1. Detect the input language and return all values in that language.
        2. NO QUANTITY REDUCTION: Do not suggest "reducing" the amount (e.g., 4 spoons to 2). You must suggest a REPLACEMENT ingredient (e.g., Oil to Applesauce).
        3. SELECTIVE SWAPPING: Do not suggest a replacement for every ingredient. Only swap high-calorie items (fats, sugars, oils, refined carbs) where a REAL caloric reduction is possible. .
        4. If an ingredient is already low calory, do not swap it unless there is a much lighter version.
        5. Make sure "calories_saved" is a number representing the estimated calories saved per portion.
		6. NO FAKE SAVINGS: Do not swap flours (White/Spelt/Wheat) for calorie reduction—they are the same. Do not suggest almond flour for calorie reduction—it is higher in calories.
		7. RECIPE STRUCTURE: Ensure the swap is culinarily sound
        
        Return ONLY JSON: 
        {
          "detected_ingredients": "item1, item2, ...",
          "swaps": [{"original": "item", "replacement": "item", "benefit": "description", "calories_saved": 0}]
        }
        Keep JSON keys in English.
                		""");
                break;
            default:
                throw new IllegalArgumentException("Unknown Operation: " + request.getOperation());
        }
        prompt.append(request.getContent());
        return prompt.toString();
    }
	

}
