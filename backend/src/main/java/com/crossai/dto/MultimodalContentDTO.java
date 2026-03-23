package com.crossai.dto;

import com.crossai.model.MultimodalContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultimodalContentDTO {
    private Long id;
    private Long inputId;
    private MultimodalContent.ContentType contentType;
    private String generatedTitle;
    private List<String> generatedBullets;
    private String generatedDescription;
    private List<String> generatedKeywords;
    private Map<String, Object> imageAssets;
    private Map<String, Object> platformVariants;
    private Map<String, Object> performanceMetrics;
    private LocalDateTime createdAt;
    
    public enum ContentType {
        TEXT_ONLY, IMAGE_ONLY, MIXED
    }
}