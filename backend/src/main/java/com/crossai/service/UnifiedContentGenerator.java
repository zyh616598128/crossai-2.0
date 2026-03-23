package com.crossai.service;

import com.crossai.dto.MultimodalContentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnifiedContentGenerator {
    
    private final TextGenerationEngine textGenerationEngine;
    private final ImageGenerationEngine imageGenerationEngine;
    private final PlatformAdapter platformAdapter;
    
    public MultimodalContentDTO generateUnifiedContent(Map<String, Object> inputData, 
                                                      Map<String, Object> generationParams) {
        log.info("Starting unified content generation");
        
        // 生成文本内容
        MultimodalContentDTO textContent = generateTextContent(inputData, generationParams);
        
        // 生成视觉内容
        MultimodalContentDTO visualContent = generateVisualContent(inputData, generationParams);
        
        // 融合内容
        MultimodalContentDTO unifiedContent = fuseContentComponents(textContent, visualContent, generationParams);
        
        // 平台适配
        String targetPlatform = (String) generationParams.getOrDefault("target_platform", "amazon");
        Map<String, Object> platformAdaptation = platformAdapter.adaptContentForPlatform(unifiedContent, targetPlatform);
        unifiedContent.setPlatformVariants(Map.of(targetPlatform, platformAdaptation));
        
        return unifiedContent;
    }
    
    private MultimodalContentDTO generateTextContent(Map<String, Object> inputData, Map<String, Object> params) {
        // 简化实现
        return MultimodalContentDTO.builder()
                .contentType(MultimodalContentDTO.ContentType.TEXT_ONLY)
                .generatedTitle("Generated Product Title")
                .generatedDescription("Generated product description based on input data")
                .build();
    }
    
    private MultimodalContentDTO generateVisualContent(Map<String, Object> inputData, Map<String, Object> params) {
        // 简化实现
        return MultimodalContentDTO.builder()
                .contentType(MultimodalContentDTO.ContentType.IMAGE_ONLY)
                .build();
    }
    
    private MultimodalContentDTO fuseContentComponents(MultimodalContentDTO textContent,
                                                     MultimodalContentDTO visualContent,
                                                     Map<String, Object> params) {
        return MultimodalContentDTO.builder()
                .contentType(MultimodalContentDTO.ContentType.MIXED)
                .generatedTitle(textContent.getGeneratedTitle())
                .generatedDescription(textContent.getGeneratedDescription())
                .build();
    }
    
    public List<MultimodalContentDTO> generateBatchContent(List<Map<String, Object>> inputDataList,
                                                          Map<String, Object> commonParams) {
        return inputDataList.stream()
                .map(inputData -> generateUnifiedContent(inputData, commonParams))
                .toList();
    }
}