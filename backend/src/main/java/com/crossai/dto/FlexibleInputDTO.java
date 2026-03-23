package com.crossai.dto;

import com.crossai.model.FlexibleInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlexibleInputDTO {
    private Long id;
    private Long userId;
    private FlexibleInput.InputMode inputMode;
    private Map<String, Object> inputData;
    private Map<String, Object> detectedIntent;
    private Map<String, Object> selectedWorkflow;
    private Integer processingTimeMs;
    private Double userSatisfactionScore;
    private LocalDateTime createdAt;
    
    public enum InputMode {
        KEYWORD_FIRST, IMAGE_FIRST, HYBRID_INTELLIGENCE
    }
}