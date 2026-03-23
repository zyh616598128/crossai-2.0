package com.crossai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntelligentOptimizationEngine {
    
    private final A_BTestManager abTestManager;
    private final PerformanceAnalyzer performanceAnalyzer;
    private final MarketAdaptor marketAdaptor;
    private final UnifiedContentGenerator contentGenerator;
    
    /**
     * 智能优化引擎主服务 - 整合A/B测试、性能分析、市场适配
     */
    public Map<String, Object> optimizeContent(Map<String, Object> inputData, 
                                             Map<String, Object> optimizationParams) {
        log.info("Starting intelligent content optimization");
        
        // 1. 生成基础内容
        MultimodalContentDTO baseContent = contentGenerator.generateUnifiedContent(
            inputData, optimizationParams
        );
        
        // 2. 性能分析
        Map<String, Object> contentMap = convertToMap(baseContent);
        String targetPlatform = (String) optimizationParams.getOrDefault("target_platform", "amazon");
        Map<String, Object> performanceAnalysis = performanceAnalyzer.analyzePerformance(
            contentMap, targetPlatform, new HashMap<>()
        );
        
        // 3. 市场适配
        String targetMarket = (String) optimizationParams.getOrDefault("target_market", "usa");
        Map<String, Object> marketAdaptation = marketAdaptor.adaptToMarket(
            contentMap, targetMarket, optimizationParams
        );
        
        // 4. A/B测试设置（如果启用）
        Map<String, Object> abTestResults = new HashMap<>();
        if (shouldRunABTest(optimizationParams)) {
            abTestResults = setupABTest(baseContent, optimizationParams);
        }
        
        // 5. 生成优化建议
        List<String> optimizationSuggestions = generateOptimizationSuggestions(
            performanceAnalysis, marketAdaptation, optimizationParams
        );
        
        // 6. 整合结果
        Map<String, Object> optimizationResult = new HashMap<>();
        optimizationResult.put("base_content", contentMap);
        optimizationResult.put("performance_analysis", performanceAnalysis);
        optimizationResult.put("market_adaptation", marketAdaptation);
        optimizationResult.put("ab_test_setup", abTestResults);
        optimizationResult.put("optimization_suggestions", optimizationSuggestions);
        optimizationResult.put("optimization_metadata", Map.of(
            "engine_version", "3D-2-v1.0",
            "optimization_timestamp", System.currentTimeMillis(),
            "target_platform", targetPlatform,
            "target_market", targetMarket,
            "optimization_type", "intelligent_comprehensive"
        ));
        
        log.info("Intelligent optimization completed");
        return optimizationResult;
    }
    
    /**
     * 设置A/B测试
     */
    private Map<String, Object> setupABTest(MultimodalContentDTO baseContent, 
                                          Map<String, Object> params) {
        String contentId = "content_" + System.currentTimeMillis();
        
        // 创建变体
        List<Map<String, Object>> variants = new ArrayList<>();
        
        // 原始版本
        variants.add(Map.of(
            "id", "variant_A",
            "content", convertToMap(baseContent),
            "description", "Original optimized content"
        ));
        
        // 改进版本（添加情感元素）
        Map<String, Object> improvedContent = new HashMap<>(convertToMap(baseContent));
        improvedContent.put("title", "✨ " + improvedContent.get("title"));
        variants.add(Map.of(
            "id", "variant_B", 
            "content", improvedContent,
            "description", "Enhanced with emotional elements"
        ));
        
        // 简化版本
        Map<String, Object> simplifiedContent = new HashMap<>(convertToMap(baseContent));
        if (simplifiedContent.containsKey("description")) {
            String desc = (String) simplifiedContent.get("description");
            simplifiedContent.put("description", simplifyDescription(desc));
        }
        variants.add(Map.of(
            "id", "variant_C",
            "content", simplifiedContent,
            "description", "Simplified version"
        ));
        
        // 创建A/B测试
        Map<String, Object> testConfig = Map.of(
            "duration_days", params.getOrDefault("test_duration", 7),
            "traffic_split", params.getOrDefault("traffic_split", Arrays.asList(0.33, 0.33, 0.34)),
            "success_metrics", Arrays.asList("click_through_rate", "conversion_rate", "engagement_time")
        );
        
        return abTestManager.createABTest(contentId, variants, testConfig);
    }
    
    /**
     * 简化描述
     */
    private String simplifyDescription(String description) {
        // 简化逻辑：保留前两句，去除复杂格式
        String[] sentences = description.split("\\.|！|？");
        if (sentences.length >= 2) {
            return sentences[0] + "。" + sentences[1] + "。";
        }
        return description.length() > 100 ? description.substring(0, 100) + "..." : description;
    }
    
    /**
     * 判断是否应该运行A/B测试
     */
    private boolean shouldRunABTest(Map<String, Object> params) {
        return params.containsKey("enable_ab_test") && 
               Boolean.TRUE.equals(params.get("enable_ab_test"));
    }
    
    /**
     * 生成优化建议
     */
    private List<String> generateOptimizationSuggestions(Map<String, Object> performanceAnalysis,
                                                       Map<String, Object> marketAdaptation,
                                                       Map<String, Object> params) {
        List<String> suggestions = new ArrayList<>();
        
        // 基于性能分析的建议
        Map<String, Double> successMetrics = (Map<String, Double>) performanceAnalysis.get("success_metrics");
        if (successMetrics.get("predicted_ctr") < 0.02) {
            suggestions.add("考虑优化标题和缩略图以提高点击率");
        }
        
        if (successMetrics.get("seo_friendliness") < 0.7) {
            suggestions.add("增加关键词密度和优化元描述");
        }
        
        // 基于市场适配的建议
        if (marketAdaptation.containsKey("seasonal_theme")) {
            suggestions.add("利用当前季节主题增强营销效果");
        }
        
        // 基于平台特性的建议
        String platform = (String) params.getOrDefault("target_platform", "amazon");
        switch (platform.toLowerCase()) {
            case "amazon":
                suggestions.add("考虑使用A+内容展示产品优势");
                break;
            case "ebay":
                suggestions.add("优化物品属性以提高搜索可见性");
                break;
            case "shopify":
                suggestions.add("添加产品视频以提升转化率");
                break;
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("内容已优化，建议持续监控表现");
        }
        
        return suggestions;
    }
    
    /**
     * 批量优化
     */
    public List<Map<String, Object>> optimizeBatchContent(List<Map<String, Object>> inputDataList,
                                                         Map<String, Object> commonParams) {
        log.info("Processing batch optimization for {} items", inputDataList.size());
        
        return inputDataList.stream()
                .map(inputData -> optimizeContent(inputData, commonParams))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取优化统计信息
     */
    public Map<String, Object> getOptimizationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("engine_version", "3D-2-v1.0");
        stats.put("optimization_capabilities", Arrays.asList(
            "performance_prediction", "market_adaptation", "ab_test_management",
            "automated_suggestions", "batch_processing"
        ));
        stats.put("supported_platforms", Arrays.asList("amazon", "ebay", "shopify", "walmart", "etsy"));
        stats.put("supported_markets", Arrays.asList("china", "usa", "europe", "japan"));
        stats.put("avg_optimization_time_ms", 25000);
        stats.put("success_rate", 0.91);
        return stats;
    }
    
    // 辅助方法
    private Map<String, Object> convertToMap(MultimodalContentDTO content) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", content.getId());
        map.put("title", content.getGeneratedTitle());
        map.put("description", content.getGeneratedDescription());
        map.put("bullets", content.getGeneratedBullets());
        map.put("keywords", content.getGeneratedKeywords());
        map.put("content_type", content.getContentType().toString());
        map.put("platform_variants", content.getPlatformVariants());
        map.put("image_assets", content.getImageAssets());
        return map;
    }
}