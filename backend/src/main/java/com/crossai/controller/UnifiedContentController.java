package com.crossai.controller;

import com.crossai.dto.MultimodalContentDTO;
import com.crossai.service.PlatformAdapter;
import com.crossai.service.UnifiedContentGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/unified-content")
@RequiredArgsConstructor
public class UnifiedContentController {
    
    private final UnifiedContentGenerator contentGenerator;
    private final PlatformAdapter platformAdapter;
    
    /**
     * 生成统一内容
     */
    @PostMapping("/generate")
    public ResponseEntity<MultimodalContentDTO> generateUnifiedContent(
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> inputData = (Map<String, Object>) request.get("input_data");
        Map<String, Object> generationParams = (Map<String, Object>) request.get("generation_params");
        
        MultimodalContentDTO result = contentGenerator.generateUnifiedContent(inputData, generationParams);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 批量生成内容
     */
    @PostMapping("/generate/batch")
    public ResponseEntity<List<MultimodalContentDTO>> generateBatchContent(
            @RequestBody Map<String, Object> request) {
        
        List<Map<String, Object>> inputDataList = (List<Map<String, Object>>) request.get("input_data_list");
        Map<String, Object> commonParams = (Map<String, Object>) request.get("common_params");
        
        List<MultimodalContentDTO> results = contentGenerator.generateBatchContent(inputDataList, commonParams);
        return ResponseEntity.ok(results);
    }
    
    /**
     * 平台适配
     */
    @PostMapping("/adapt-platform")
    public ResponseEntity<Map<String, Object>> adaptForPlatform(
            @RequestBody Map<String, Object> request) {
        
        MultimodalContentDTO content = (MultimodalContentDTO) request.get("content");
        String platform = (String) request.get("platform");
        
        Map<String, Object> adapted = platformAdapter.adaptContentForPlatform(content, platform);
        return ResponseEntity.ok(adapted);
    }
    
    /**
     * 多平台适配
     */
    @PostMapping("/adapt-multiple-platforms")
    public ResponseEntity<Map<String, Map<String, Object>>> adaptForMultiplePlatforms(
            @RequestBody Map<String, Object> request) {
        
        MultimodalContentDTO content = (MultimodalContentDTO) request.get("content");
        List<String> platforms = (List<String>) request.get("platforms");
        
        Map<String, Map<String, Object>> adaptations = 
            platformAdapter.adaptContentForMultiplePlatforms(content, platforms);
        return ResponseEntity.ok(adaptations);
    }
    
    /**
     * 获取生成统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getGenerationStats() {
        Map<String, Object> stats = contentGenerator.getGenerationStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 获取支持的平台
     */
    @GetMapping("/platforms")
    public ResponseEntity<Map<String, Object>> getSupportedPlatforms() {
        Map<String, Object> platforms = Map.of(
            "supported_platforms", List.of(
                Map.of("name", "amazon", "display_name", "Amazon", "max_title_length", 200),
                Map.of("name", "ebay", "display_name", "eBay", "max_title_length", 80),
                Map.of("name", "shopify", "display_name", "Shopify", "max_title_length", 70),
                Map.of("name", "walmart", "display_name", "Walmart", "max_title_length", 75),
                Map.of("name", "etsy", "display_name", "Etsy", "max_title_length", 140)
            ),
            "adaptation_features", List.of(
                "title_optimization", "description_formatting", "keyword_optimization",
                "platform_specific_fields", "html_markdown_support"
            )
        );
        return ResponseEntity.ok(platforms);
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> health = Map.of(
            "status", "healthy",
            "service", "unified-content-generator",
            "version", "3D-1-v1.0",
            "capabilities", List.of(
                "unified_content_generation",
                "multi_platform_adaptation",
                "text_image_fusion",
                "batch_processing",
                "platform_specific_optimization"
            )
        );
        return ResponseEntity.ok(health);
    }
}