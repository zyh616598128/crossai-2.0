package com.crossai.service;

import com.crossai.dto.FlexibleInputDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class InputDetectionService {
    
    public String detectInputMode(Map<String, Object> inputData) {
        if (inputData == null || inputData.isEmpty()) {
            return "keyword_first";
        }
        
        if (isTextDominant(inputData)) {
            return isDetailedDescription(inputData) ? "enhanced_text" : "keyword_first";
        }
        
        if (isImageDominant(inputData)) {
            return hasMultipleImages(inputData) ? "comparative_image" : "image_first";
        }
        
        if (hasBothModalities(inputData)) {
            return "hybrid_intelligence";
        }
        
        return "keyword_first";
    }
    
    private boolean isTextDominant(Map<String, Object> inputData) {
        int textWeight = 0;
        int imageWeight = 0;
        
        if (inputData.containsKey("text")) {
            String text = (String) inputData.get("text");
            if (text != null) textWeight += text.length() / 100;
        }
        
        if (inputData.containsKey("images")) {
            Object images = inputData.get("images");
            if (images instanceof java.util.List) {
                imageWeight += ((java.util.List<?>) images).size() * 3;
            }
        }
        
        return textWeight > imageWeight;
    }
    
    private boolean isImageDominant(Map<String, Object> inputData) {
        int imageWeight = 0;
        int textWeight = 0;
        
        if (inputData.containsKey("images")) {
            Object images = inputData.get("images");
            if (images instanceof java.util.List) {
                imageWeight += ((java.util.List<?>) images).size() * 3;
            }
        }
        
        if (inputData.containsKey("text")) {
            String text = (String) inputData.get("text");
            if (text != null) textWeight += text.length() / 150;
        }
        
        return imageWeight > textWeight;
    }
    
    private boolean hasMultipleImages(Map<String, Object> inputData) {
        if (inputData.containsKey("images")) {
            Object images = inputData.get("images");
            if (images instanceof java.util.List) {
                return ((java.util.List<?>) images).size() > 1;
            }
        }
        return false;
    }
    
    private boolean hasBothModalities(Map<String, Object> inputData) {
        boolean hasText = inputData.containsKey("text") || inputData.containsKey("keywords");
        boolean hasImages = inputData.containsKey("images") || inputData.containsKey("image_urls");
        return hasText && hasImages;
    }
    
    private boolean isDetailedDescription(Map<String, Object> inputData) {
        if (inputData.containsKey("description")) {
            String desc = (String) inputData.get("description");
            return desc != null && desc.length() > 200;
        }
        return false;
    }
    
    public Map<String, Object> analyzeIntent(Map<String, Object> inputData, String detectedMode) {
        Map<String, Object> intent = new HashMap<>();
        intent.put("input_mode", detectedMode);
        intent.put("timestamp", LocalDateTime.now());
        intent.put("confidence", calculateConfidence(inputData, detectedMode));
        
        switch (detectedMode) {
            case "keyword_first":
                intent.put("primary_goal", "generate_listing_from_keywords");
                break;
            case "image_first":
                intent.put("primary_goal", "generate_content_from_images");
                break;
            case "hybrid_intelligence":
                intent.put("primary_goal", "fuse_text_and_visual_inputs");
                break;
            default:
                intent.put("primary_goal", "generate_listing");
        }
        
        return intent;
    }
    
    private double calculateConfidence(Map<String, Object> inputData, String mode) {
        return 0.8; // 简化实现
    }
    
    public FlexibleInputDTO createFlexibleInput(Long userId, Map<String, Object> inputData) {
        String detectedMode = detectInputMode(inputData);
        Map<String, Object> detectedIntent = analyzeIntent(inputData, detectedMode);
        
        return FlexibleInputDTO.builder()
                .userId(userId)
                .inputMode(FlexibleInputDTO.InputMode.valueOf(detectedMode.toUpperCase().replace("-", "_")))
                .inputData(inputData)
                .detectedIntent(detectedIntent)
                .selectedWorkflow(detectedIntent)
                .build();
    }
}