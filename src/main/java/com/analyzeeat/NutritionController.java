package com.analyzeeat;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/nutrition")
@CrossOrigin(origins = "*")
@AllArgsConstructor

public class NutritionController {
	private final NutritionService nutritionService;

	@PostMapping("/analyze")
	public Mono<ResponseEntity<Map<String, Object>>> analyzeIngredients(@RequestBody AnalysisRequest request){
	    return nutritionService.analyzeIngredients(request)
	            .map(ResponseEntity::ok)
	            .onErrorResume(e -> Mono.just(
	                ResponseEntity.status(500).body(Map.of("error", e.getMessage()))
	            ));
	}
	}

